package com.varian.oiscn.core.identifier;

/**
 * Created by gbt1220 on 3/28/2017.
 * <p>
 * This value set has an inline code system http://hl7.org/fhir/identifier-use, which defines the following codes:
 * usual                The encounterIdentifier recommended for display and use in real-world interactions.
 * official                The encounterIdentifier considered to be most trusted for the identification of this item.
 * temp                A temporary encounterIdentifier.
 * secondary An encounterIdentifier that was assigned in secondary use - it serves to identify the object in a relative context, but cannot be consistently assigned to the same object again in a different context.
 */
public enum IdentifierUseEnum {
    USUAL,
    OFFICIAL,
    TEMP,
    SECONDARY,
}
