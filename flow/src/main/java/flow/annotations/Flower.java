package flow.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * annotates provider-methods for {@link AnnotationContract}.
 * Input parameters define dependencies to this provider and result type defines output dependency
 * <pre>{@code 
 *  public static class ExampleProvider {
 *		@Flower
 *		public ProvidedType execute(InputObject object) {
 *			...
 *		}
 *	}
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Flower {

}
