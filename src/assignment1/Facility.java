package assignment1;

/**
 * An immutable representation of a facility. Such a facility has a unique identifier. Two
 * facilities are equal when they have the same identifier.
 * <p>
 * DO NOT MODIFY THIS FILE IN ANY WAY.
 */
public final class Facility {

    // the unique identifier of the facility
    private final int identifier;

    /**
     * Creates a new facility with the given identifier.
     */
    public Facility(int identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the identifier of the facility.
     */
    public int identifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "F" + identifier;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Facility other)) {
            return false;
        }
        return (this.identifier == other.identifier);
    }

    @Override
    public int hashCode() {
        return identifier;
    }
}
