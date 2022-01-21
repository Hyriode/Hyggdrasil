package fr.hyriode.hyggdrasil.api.util.builder;

import java.util.function.Supplier;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 12/12/2021 at 14:29
 */
public class BuilderEntry<T> {

    /** Option's name */
    private final String name;
    /** Option's value */
    private Supplier<T> objectSupplier;
    /** Is option's required */
    private boolean required;

    /**
     * Constructor of {@link BuilderEntry}
     *
     * @param name Entry's name
     */
    public BuilderEntry(String name) {
        this.name = name;
    }

    /**
     * Constructor of {@link BuilderEntry}
     *
     * @param name Entry's name
     * @param defaultValue Default entry's value
     */
    public BuilderEntry(String name, Supplier<T> defaultValue) {
        this.name = name;
        this.objectSupplier = defaultValue;
    }

    /**
     * Get given entry's value
     *
     * @return {@link T} object
     * @throws BuildException if the entry is required and the value is null
     */
    public T get() throws BuildException {
        if (this.required && this.objectSupplier == null) {
            throw new BuildException("Builder option: " + this.name + " is required but its value is null!");
        }
        return this.objectSupplier.get();
    }

    /**
     * Get entry's object value but as a {@link Supplier}
     *
     * @return A {@link Supplier}
     */
    public Supplier<T> getAsSupplier() {
        return this.objectSupplier;
    }

    /**
     * Set entry's value
     *
     * @param objectSupplier New entry's value
     * @return {@link BuilderEntry}
     */
    public BuilderEntry<T> set(Supplier<T> objectSupplier) {
        this.objectSupplier = objectSupplier;
        return this;
    }

    /**
     * Set entry not required
     *
     * @return {@link BuilderEntry}
     */
    public BuilderEntry<T> optional() {
        this.required = false;
        return this;
    }

    /**
     * Set entry required
     *
     * @return {@link BuilderEntry}
     */
    public BuilderEntry<T> required() {
        this.required = true;
        return this;
    }

    /**
     * Set if this entry depends on other entries
     *
     * @return {@link BuilderEntry}
     */
    public BuilderEntry<T> require(BuilderEntry<?>... requiredOptions) {
        for (BuilderEntry<?> option : requiredOptions) {
            option.required = true;
        }
        return this;
    }

    /**
     * Check if the entry is required
     *
     * @return <code>true</code> if yes
     */
    public boolean isRequired() {
        return this.required;
    }

}
