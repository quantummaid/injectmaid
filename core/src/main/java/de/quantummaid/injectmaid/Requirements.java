package de.quantummaid.injectmaid;

import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.reflectmaid.typescanner.requirements.DetectionRequirements;
import de.quantummaid.reflectmaid.typescanner.requirements.RequirementName;

import static de.quantummaid.injectmaid.api.ReusePolicy.*;

public final class Requirements {
    public static final RequirementName REGISTERED = new RequirementName("registered");
    public static final RequirementName SINGLETON = new RequirementName("singleton");
    public static final RequirementName EAGER = new RequirementName("eager");
    public static final RequirementName LAZY = new RequirementName("lazy");

    public static ReusePolicy extract(final DetectionRequirements requirements) {
        if (requirements.requires(SINGLETON)) {
            if (requirements.requires(LAZY)) {
                return LAZY_SINGLETON;
            } else if (requirements.requires(EAGER)) {
                return EAGER_SINGLETON;
            } else {
                return DEFAULT_SINGLETON;
            }
        } else {
            return ReusePolicy.PROTOTYPE;
        }
    }
}
