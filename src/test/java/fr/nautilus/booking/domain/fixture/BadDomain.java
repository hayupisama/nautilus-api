package fr.nautilus.booking.domain.fixture;

/** Violation volontaire (fixture du test de detection) : le domaine depend de jakarta. */
public class BadDomain {
    public jakarta.ws.rs.core.Response leak() {
        return null;
    }
}
