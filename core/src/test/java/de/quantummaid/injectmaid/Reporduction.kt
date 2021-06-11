package de.quantummaid.injectmaid

import de.quantummaid.injectmaid.InjectMaid.anInjectMaid

class TestSuiteScope
class TestClassScope
class TestCaseScope


fun main() {
    anInjectMaid()
        .withLifecycleManagement()
        .closeOnJvmShutdown()
        .withScope(TestSuiteScope::class.java) { testSuiteInjectMaid ->
            testSuiteInjectMaid.withCustomType(Int::class.java) { 1 }
            //testSuiteInjectMaid.withScope(TestClassScope::class.java) { testClassInjectMaid ->
                /*
                testClassInjectMaid.withScope(TestCaseScope::class.java) { testCaseInjectMaid ->
                }
                 */
            //}
        }
        .withScope(TestSuiteScope::class.java) { testSuiteInjectMaid ->
            testSuiteInjectMaid.withCustomType(String::class.java) { "" }
            //testSuiteInjectMaid.withScope(TestClassScope::class.java) { testClassInjectMaid ->
                /*
                testClassInjectMaid.withScope(TestCaseScope::class.java) { testCaseInjectMaid ->
                }
                 */
            //}
        }
        .build()
}