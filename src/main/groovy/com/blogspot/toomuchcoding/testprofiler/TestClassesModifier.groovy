package com.blogspot.toomuchcoding.testprofiler

import com.blogspot.toomuchcoding.testprofiler.TestProfilerPluginExtension.BuildBreakerOptions
import groovy.io.FileType
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import javassist.*
import javassist.bytecode.AnnotationsAttribute
import javassist.bytecode.ClassFile
import javassist.bytecode.ConstPool
import javassist.bytecode.annotation.Annotation
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

import static javassist.bytecode.AnnotationsAttribute.visibleTag

@PackageScope
@Slf4j
@CompileStatic
class TestClassesModifier {

    private static final String CLASS_FILE_EXTENSION = 'class'
    private static final String JUNIT_RULE_FQN = "org.junit.Rule"

    private final Project project
    private final BuildBreakerOptions buildBreakerOptions

    TestClassesModifier(Project project, BuildBreakerOptions buildBreakerOptions) {
        this.project = project
        this.buildBreakerOptions = buildBreakerOptions
    }

    void appendRule(Test test) {
        log.debug("Appending Timeout rule to test [$test]")
        List<String> pathsToLoad = retrieveFqnsOfClasses(test)
        log.debug("PathsToLoad $pathsToLoad")
        Collection<String> nonIgnorableClasses = findAllNonIgnorableClasses(pathsToLoad)
        log.debug("NonIgnorableClasses $nonIgnorableClasses")
        GroovyClassLoader groovyClassLoader = new TestTaskBasedGroovyClassLoader(test)
        overwriteExistingTestClasses(nonIgnorableClasses, groovyClassLoader, test)
    }

    private List<String> retrieveFqnsOfClasses(Test test) {
        List<String> pathsToLoad = []
        log.debug("TestClassNameSuffixes [$buildBreakerOptions.testClassNameSuffixes], test classes dir [$test.testClassesDir]")
        test.testClassesDir.eachFileRecurse(FileType.FILES) { File file ->
            log.debug("TestFileName [$file.name]")
            if ( buildBreakerOptions.testClassNameSuffixes.any { file.name.endsWith("${it}.${CLASS_FILE_EXTENSION}") }) {
                pathsToLoad << createFQNFromFiles(file, test.testClassesDir)
            }
        }
        return pathsToLoad
    }

    private Collection<String> findAllNonIgnorableClasses(List<String> pathsToLoad) {
        List<String> regexpsToIgnore = buildBreakerOptions.testClassRegexpsToIgnore
        log.debug("Ignoring the following test classes matching the regexps [${regexpsToIgnore}]")
        return pathsToLoad.findAll { String path -> !regexpsToIgnore.any { path.matches(it) } }
    }

    private String createFQNFromFiles(File testClass, File testClassesDir) {
        return (testClass.absolutePath - testClassesDir.absolutePath - File.separator - ".$CLASS_FILE_EXTENSION").replaceAll(File.separator, '.')
    }

    private CtField createTimeoutField(CtClass testClass) {
        return CtField.make("public org.junit.rules.Timeout ${getGeneratedFieldName(testClass)} = new org.junit.rules.Timeout($buildBreakerOptions.maxTestThreshold);", testClass)
    }

    private String getGeneratedFieldName(CtClass testClass) {
        return "${testClass.simpleName}_timeout"
    }

    private void wrapFieldWithRuleAnnotation(ConstPool constpool, CtField field) {
        AnnotationsAttribute attr = new AnnotationsAttribute(constpool, visibleTag)
        Annotation annot = new Annotation(JUNIT_RULE_FQN, constpool)
        attr.addAnnotation(annot)
        field.fieldInfo.addAttribute(attr)
    }

    private void overwriteExistingTestClasses(Collection<String> pathsToLoad, ClassLoader classLoader, Test test) {
        pathsToLoad.collect { classLoader.loadClass(it) }.each { writeFile(it, test) }
    }

    private void writeFile(Class clazz, Test test) {
        ClassPool pool = ClassPool.getDefault()
        ConstPool constpool = createConstPool(clazz, pool)
        CtClass testClass = pool.get(clazz.name)
        if (theFieldHasBeenAlreadyCreated(testClass)) {
            return
        }
        CtField field = createTimeoutField(testClass)
        wrapFieldWithRuleAnnotation(constpool, field)
        testClass.addField(field)
        testClass.writeFile(test.testClassesDir.absolutePath)
    }

    private boolean theFieldHasBeenAlreadyCreated(CtClass testClass) {
        try {
            return testClass.getField(getGeneratedFieldName(testClass))
        } catch (NotFoundException exception) {
            return false
        }
    }

    private ConstPool createConstPool(Class clazz, ClassPool pool) {
        ClassClassPath classClassPath = new ClassClassPath(clazz)
        pool.insertClassPath(classClassPath)
        CtClass ctClass = pool.getCtClass(clazz.name)
        ctClass.defrost()
        ClassFile ccFile = ctClass.getClassFile()
        return ccFile.getConstPool()
    }

}
