package de.quantummaid.injectmaid.api.customtype;

import de.quantummaid.injectmaid.api.ReusePolicy;
import de.quantummaid.injectmaid.instantiator.Instantiator;
import de.quantummaid.injectmaid.statemachine.InjectMaidTypeScannerResult;
import de.quantummaid.reflectmaid.typescanner.Context;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.factories.StateFactory;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import de.quantummaid.reflectmaid.typescanner.states.StatefulDefinition;
import de.quantummaid.reflectmaid.typescanner.states.detected.Unreasoned;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static de.quantummaid.injectmaid.statemachine.InjectMaidTypeScannerResult.result;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomInstantiatorFactory implements StateFactory<InjectMaidTypeScannerResult> {
    private final TypeIdentifier typeIdentifier;
    private final Scope scope;
    private final Instantiator instantiator;
    private final ReusePolicy reusePolicy;

    public static CustomInstantiatorFactory customInstantiatorFactory(final TypeIdentifier typeIdentifier,
                                                                      final Scope scope,
                                                                      final Instantiator instantiator,
                                                                      final ReusePolicy reusePolicy) {
        return new CustomInstantiatorFactory(typeIdentifier, scope, instantiator, reusePolicy);
    }

    @Nullable
    @Override
    public StatefulDefinition<InjectMaidTypeScannerResult> create(@NotNull final TypeIdentifier type,
                                                                  @NotNull final Context<InjectMaidTypeScannerResult> context) {
        if (!typeIdentifier.equals(type)) {
            return null;
        }
        if (!scope.contains(context.getScope())) {
            return null;
        }
        final InjectMaidTypeScannerResult result = result(typeIdentifier, scope, instantiator, reusePolicy);
        context.setManuallyConfiguredResult(result);
        return new Unreasoned<>(context);
    }
}
