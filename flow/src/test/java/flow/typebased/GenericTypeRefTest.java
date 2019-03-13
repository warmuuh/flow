package flow.typebased;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

class GenericTypeRefTest {

	@Test
	void shouldKeepGenericTypeParameter() {
		TypeRef typeRef = new GenericTypeRef<List<String>>() {};
		assertThat(typeRef.genericType.getTypeName()).isEqualTo("java.util.List<java.lang.String>");
	}
	@Test
	void shouldWorkForNormalTypesToo() {
		TypeRef typeRef = new GenericTypeRef<String>() {};
		assertThat(typeRef.genericType.getTypeName()).isEqualTo("java.lang.String");
	}

	@Test
	void shouldThrowExceptionIfNoSubclassWasCreated() {
		assertThatThrownBy(() -> new GenericTypeRef<String>())
			.isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	void equalsTransparently() {
		assertThat(new GenericTypeRef<String>() {}).isEqualTo(new TypeRef(String.class));
	}
}
