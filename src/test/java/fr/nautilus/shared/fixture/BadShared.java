package fr.nautilus.shared.fixture;

import fr.nautilus.booking.application.fixture.BadUseCase;

/** Violation volontaire (fixture du test de detection) : shared depend d'un module. */
public class BadShared {
    BadUseCase useCase;
}
