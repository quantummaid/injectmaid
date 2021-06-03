package de.quantummaid.injectmaid.statemachine;

import de.quantummaid.injectmaid.Definition;
import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.injectmaid.detection.Detectors;
import de.quantummaid.injectmaid.detection.SingletonSwitch;
import de.quantummaid.injectmaid.instantiator.Instantiator;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.requirements.DetectionRequirements;
import de.quantummaid.reflectmaid.typescanner.states.DetectionResult;
import de.quantummaid.reflectmaid.typescanner.states.Detector;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import static de.quantummaid.injectmaid.Requirements.extract;
import static de.quantummaid.injectmaid.detection.SingletonSwitch.singletonSwitch;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InjectMaidDetector implements Detector<Definition> {

    public static InjectMaidDetector injectMaidDetector() {
        return new InjectMaidDetector();
    }

    @NotNull
    @Override
    public DetectionResult<Definition> detect(@NotNull TypeIdentifier type, @NotNull DetectionRequirements detectionRequirements) {
        final ReusePolicy oldReusePolicy = extract(detectionRequirements);
        final SingletonSwitch singletonSwitch = singletonSwitch(oldReusePolicy);
        //final DetectionResult<Instantiator> result = Detectors.detect(type, singletonSwitch);


        return null;
    }
}
