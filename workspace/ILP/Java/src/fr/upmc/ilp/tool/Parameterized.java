// $Id$
package fr.upmc.ilp.tool;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/** This class is a cloned version of the org.junit.runners.Parameterized
 * class. It modifies a single method, the getName method, that is used
 * by Eclipse to display the name of the JUnit4 test.
 * */

public class Parameterized extends Suite {
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface Parameters {}

    private class TestClassRunnerForParameters 
        extends BlockJUnit4ClassRunner {
        private final int fParameterSetNumber;

        private final List<Object[]> fParameterList;

        TestClassRunnerForParameters(Class<?> type,
                List<Object[]> parameterList, int i) throws InitializationError {
            super(type);
            fParameterList= parameterList;
            fParameterSetNumber= i;
        }

        @Override
        public Object createTest() throws Exception {
            return getTestClass().getOnlyConstructor().newInstance(
                    computeParams());
        }

        private Object[] computeParams() throws Exception {
            try {
                return fParameterList.get(fParameterSetNumber);
            } catch (ClassCastException e) {
                throw new Exception(String.format(
                        "%s.%s() must return a Collection of arrays.",
                        getTestClass().getName(), getParametersMethod(
                                getTestClass()).getName()));
            }
        }

        /* This method prints the name of the file currently processed. */
        @Override
        protected String getName() {
            File file = (File) fParameterList.get(fParameterSetNumber)[0];
            return String.format("[%s : %s]", 
                    fParameterSetNumber,
                    file.getBaseName() );
        }

        @Override
        protected String testName(final FrameworkMethod method) {
            return String.format("%s[%s]", method.getName(),
                    fParameterSetNumber);
        }

        @Override
        protected void validateZeroArgConstructor(List<Throwable> errors) {
            // constructor can, nay, should have args.
        }

        @Override
        protected Statement classBlock(RunNotifier notifier) {
            return childrenInvoker(notifier);
        }
    }

    private final ArrayList<Runner> runners= new ArrayList<>();

    /**
     * Only called reflectively. Do not use programmatically.
     */
    public Parameterized(Class<?> klass) throws Throwable {
        super(klass, Collections.<Runner>emptyList());
        List<Object[]> parametersList= getParametersList(getTestClass());
        for (int i= 0; i < parametersList.size(); i++)
            runners.add(new TestClassRunnerForParameters(getTestClass().getJavaClass(),
                    parametersList, i));
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> getParametersList(TestClass klass)
            throws Throwable {
        return (List<Object[]>) getParametersMethod(klass).invokeExplosively(
                null);
    }

    private FrameworkMethod getParametersMethod(TestClass testClass)
            throws Exception {
        List<FrameworkMethod> methods= testClass
                .getAnnotatedMethods(Parameters.class);
        for (FrameworkMethod each : methods) {
            int modifiers= each.getMethod().getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
                return each;
        }

        throw new Exception("No public static parameters method on class "
                + testClass.getName());
    }

}
