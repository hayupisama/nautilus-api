package fr.nautilus.booking.application.fixture;

import fr.nautilus.account.adapter.out.persistence.fixture.AccountJpaFixture;

/** Violation volontaire (fixture du test de detection) : un module appelle l'adaptateur d'un autre. */
public class BadUseCase {
    AccountJpaFixture other = new AccountJpaFixture();
}
