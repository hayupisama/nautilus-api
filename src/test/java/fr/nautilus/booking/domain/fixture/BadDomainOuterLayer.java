package fr.nautilus.booking.domain.fixture;

import fr.nautilus.booking.application.fixture.BadUseCase;

/** Violation volontaire (fixture du test de detection) : le domaine depend de la couche application. */
public class BadDomainOuterLayer {
    BadUseCase useCase;
}
