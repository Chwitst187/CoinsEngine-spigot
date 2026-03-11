package su.nightexpress.coinsengine.currency.placeholder;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.excellenteconomy.api.currency.ExcellentCurrency;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.currency.CurrencyRegistry;
import su.nightexpress.coinsengine.util.placeholder.PlaceholderRegistryCompat;
import su.nightexpress.nightcore.bridge.placeholder.PlaceholderProvider;
import su.nightexpress.nightcore.bridge.placeholder.PlaceholderRegistry;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.text.night.NightMessage;

public class PlayerBalancePlaceholders implements PlaceholderProvider {

    private final CurrencyRegistry currencyRegistry;
    private final CurrencyManager manager;

    public PlayerBalancePlaceholders(@NonNull CurrencyRegistry currencyRegistry, @NonNull CurrencyManager manager) {
        this.currencyRegistry = currencyRegistry;
        this.manager = manager;
    }

    @Override
    public void addPlaceholders(@NonNull PlaceholderRegistry registry) {
        this.registerCurrencyPlaceholder(registry, "payments_state", (player, currency) -> {
            return CoreLang.STATE_ENABLED_DISALBED.get(this.manager.getPaymentsState(player, currency));
        });

        this.registerCurrencyPlaceholder(registry, "balance_short_clean", (player, currency) -> {
            return NightMessage.stripTags(currency.formatCompact(this.manager.getBalance(player, currency)));
        });

        this.registerCurrencyPlaceholder(registry, "balance_short_legacy", (player, currency) -> {
            return NightMessage.asLegacy(currency.formatCompact(this.manager.getBalance(player, currency)));
        });

        this.registerCurrencyPlaceholder(registry, "balance_short", (player, currency) -> {
            return currency.formatCompact(this.manager.getBalance(player, currency));
        });

        this.registerCurrencyPlaceholder(registry, "balance_clean", (player, currency) -> {
            return NightMessage.stripTags(currency.format(this.manager.getBalance(player, currency)));
        });

        this.registerCurrencyPlaceholder(registry, "balance_legacy", (player, currency) -> {
            return NightMessage.asLegacy(currency.format(this.manager.getBalance(player, currency)));
        });

        this.registerCurrencyPlaceholder(registry, "balance_raw", (player, currency) -> {
            return currency.formatRaw(this.manager.getBalance(player, currency));
        });

        this.registerCurrencyPlaceholder(registry, "balance", (player, currency) -> {
            return currency.format(this.manager.getBalance(player, currency));
        });
    }

    private void registerCurrencyPlaceholder(@NonNull PlaceholderRegistry registry, @NonNull String key, @NonNull CurrencyPlaceholder parser) {
        PlaceholderRegistryCompat.registerCurrencyMapped(registry, this.currencyRegistry, key, parser::parse);
        PlaceholderRegistryCompat.registerRaw(registry, key, (player, payload) -> {
            if (player == null) return null;

            ExcellentCurrency currency = this.currencyRegistry.getById(payload);
            if (currency == null) return null;

            return parser.parse(player, currency);
        });
    }

    @FunctionalInterface
    private interface CurrencyPlaceholder {

        @Nullable String parse(@NonNull org.bukkit.entity.Player player, @NonNull ExcellentCurrency currency);
    }
}
