package io.github.oliviercailloux.sample_quarkus_heroku;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * https://stackoverflow.com/questions/30106476/using-javascripts-atob-to-decode-base64-doesnt-properly-decode-utf-8-strings
 *
 * <ul>
 * <li>curl -v -u "✓ à la mode:a" http://localhost:8080/v0/me</li>
 * <li>Authorization: Basic 4pyTIMOgIGxhIG1vZGU6YQ==</li>
 * <li>curl -v -u "test:123£" http://localhost:8080/v0/me</li>
 * <li>Authorization: Basic dGVzdDoxMjPCow==</li>
 *
 * Thus, as in https://datatracker.ietf.org/doc/html/rfc7617#section-2.1. And
 * all of these are decoded consistently by Quarkus.
 */
@Entity
@UserDefinition
public class User {
	public static final String ADMIN_ROLE = "Admin";

	public static final String STUDENT_ROLE = "Student";

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Username
	@NotNull
	@Column(unique = true)
	String username;

	@Password
	@NotNull
	String password;

	@Roles
	@NotNull
	String role;

	public User() {
		/* For JPA. */
	}

	public User(String username, String password, String role) {
		this.username = checkNotNull(username);
		this.password = BcryptUtil.bcryptHash(password);
		this.role = checkNotNull(role);
	}

	public String getUsername() {
		return username;
	}

	public String getRole() {
		return role;
	}

	@Override
	public boolean equals(Object o2) {
		if (!(o2 instanceof User)) {
			return false;
		}
		final User t2 = (User) o2;
		return id == t2.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("username", username).add("role", role).toString();
	}
}
