package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import controllers.Gerenciador;
import models.DadosSessaoAdmin;
import play.cache.Cache;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface User {

}
