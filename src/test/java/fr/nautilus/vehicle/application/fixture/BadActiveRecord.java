package fr.nautilus.vehicle.application.fixture;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

/** Violation volontaire (fixture du test de detection) : active record, hors couche persistance. */
public class BadActiveRecord extends PanacheEntityBase {
}
