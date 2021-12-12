package fr.hyriode.hyggdrasil.api.util.builder;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 12/12/2021 at 12:02
 */
@FunctionalInterface
public interface IBuilder<T> {

    /**
     * Build the {@link T} object
     *
     * @return The built {@link T} object
     * @throws BuildException if an error occurred during building
     */
    T build() throws BuildException;

}
