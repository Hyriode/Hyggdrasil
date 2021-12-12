package fr.hyriode.hyggdrasil.api.util.builder;

import java.util.function.Supplier;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 12/12/2021 at 14:29
 */
public class BuilderOption<T> {

    /** Option's name */
    private final String name;
    /** Option's value */
    private T object;
    /** Is option's required */
    private boolean required;

    /**
     * Constructor of {@link BuilderOption}
     *
     * @param name Option's name
     */
    public BuilderOption(String name) {
        this.name = name;
    }

    /**
     * Constructor of {@link BuilderOption}
     *
     * @param name Option's name
     * @param defaultValue Default option's value
     */
    public BuilderOption(String name, Supplier<T> defaultValue) {
        this.name = name;
        this.object = defaultValue.get();
    }

    /**
     * Get given option's value
     *
     * @return {@link T} object
     * @throws BuildException if the option is required and the value is null
     */
    public T get() throws BuildException {
        if (this.required && this.object == null) {
            throw new BuildException("Builder option: " + this.name + " is required but its value is null!");
        }
        return this.object;
    }

    /**
     * Set option's value
     *
     * @param object New option's value
     * @return {@link BuilderOption}
     */
    public BuilderOption<T> set(T object) {
        this.object = object;
        return this;
    }

    /**
     * Set option not required
     *
     * @return {@link BuilderOption}
     */
    public BuilderOption<T> optional() {
        this.required = false;
        return this;
    }

    /**
     * Set option required
     *
     * @return {@link BuilderOption}
     */
    public BuilderOption<T> required() {
        this.required = true;
        return this;
    }

    /**
     * Set if this option depends on other options
     *
     * @return {@link BuilderOption}
     */
    public BuilderOption<T> require(BuilderOption<?>... requiredOptions) {
        for (BuilderOption<?> option : requiredOptions) {
            option.required = true;
        }
        return this;
    }

    /**
     * Check if the option is required
     *
     * @return <code>true</code> if yes
     */
    public boolean isRequired() {
        return this.required;
    }

}
