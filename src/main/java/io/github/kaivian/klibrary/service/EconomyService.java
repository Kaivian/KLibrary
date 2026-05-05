package io.github.kaivian.klibrary.service;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service for interacting with the server's economy system via Vault.
 *
 * <p>This service wraps Vault's {@link Economy} provider and exposes fail-safe
 * methods for depositing, withdrawing, and checking player balances. All
 * operations gracefully handle the case where Vault is not installed.</p>
 *
 * <p>When Vault is absent, all monetary operations return {@code false} and
 * log a warning. This ensures the action/requirement engine never crashes
 * due to a missing optional dependency.</p>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * EconomyService eco = new EconomyService(dependencyService);
 * if (eco.withdraw(player, 100.0)) {
 *     // payment successful
 * }
 * }</pre>
 */
public class EconomyService {

    private static final Logger LOGGER = Logger.getLogger(EconomyService.class.getName());

    private final PluginDependencyService dependencyService;
    private Economy economy;
    private boolean initialized = false;

    /**
     * Constructs a new {@code EconomyService}.
     *
     * @param dependencyService the plugin dependency service for checking Vault
     */
    public EconomyService(PluginDependencyService dependencyService) {
        this.dependencyService = dependencyService;
    }

    /**
     * Lazily initializes the Vault economy provider.
     *
     * <p>This deferred initialization ensures the service can be constructed
     * before Vault is fully loaded, with the actual provider lookup happening
     * on first use.</p>
     */
    private void ensureInitialized() {
        if (initialized) return;
        initialized = true;

        if (!dependencyService.hasVault()) {
            LOGGER.warning("Vault not found. Economy features will be disabled.");
            return;
        }

        try {
            RegisteredServiceProvider<Economy> rsp =
                    Bukkit.getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                economy = rsp.getProvider();
            } else {
                LOGGER.warning("Vault is installed but no economy provider is registered.");
            }
        } catch (Exception e) {
            LOGGER.warning("Failed to hook into Vault economy: " + e.getMessage());
        }
    }

    /**
     * Returns the Vault {@link Economy} provider, if available.
     *
     * @return an {@link Optional} containing the economy provider, or empty
     */
    public Optional<Economy> getEconomy() {
        ensureInitialized();
        return Optional.ofNullable(economy);
    }

    /**
     * Checks whether an economy system is available.
     *
     * @return {@code true} if Vault economy is hooked
     */
    public boolean isAvailable() {
        return getEconomy().isPresent();
    }

    /**
     * Deposits money into a player's account.
     *
     * @param player the player to deposit to
     * @param amount the amount to deposit (must be positive)
     * @return {@code true} if the deposit succeeded, {@code false} otherwise
     */
    public boolean deposit(Player player, double amount) {
        return getEconomy().map(eco -> {
            EconomyResponse response = eco.depositPlayer(player, amount);
            if (!response.transactionSuccess()) {
                LOGGER.warning("Economy deposit failed for " + player.getName()
                        + ": " + response.errorMessage);
            }
            return response.transactionSuccess();
        }).orElseGet(() -> {
            LOGGER.warning("Vault not found, skipping money deposit.");
            return false;
        });
    }

    /**
     * Withdraws money from a player's account.
     *
     * @param player the player to withdraw from
     * @param amount the amount to withdraw (must be positive)
     * @return {@code true} if the withdrawal succeeded, {@code false} otherwise
     */
    public boolean withdraw(Player player, double amount) {
        return getEconomy().map(eco -> {
            EconomyResponse response = eco.withdrawPlayer(player, amount);
            if (!response.transactionSuccess()) {
                LOGGER.warning("Economy withdrawal failed for " + player.getName()
                        + ": " + response.errorMessage);
            }
            return response.transactionSuccess();
        }).orElseGet(() -> {
            LOGGER.warning("Vault not found, skipping money withdrawal.");
            return false;
        });
    }

    /**
     * Gets a player's current balance.
     *
     * @param player the player to query
     * @return the balance, or {@code 0.0} if economy is unavailable
     */
    public double getBalance(Player player) {
        return getEconomy().map(eco -> eco.getBalance(player)).orElse(0.0);
    }

    /**
     * Checks whether a player has at least the specified amount.
     *
     * @param player the player to check
     * @param amount the required amount
     * @return {@code true} if the player has sufficient funds, {@code false} otherwise
     */
    public boolean has(Player player, double amount) {
        return getEconomy().map(eco -> eco.has(player, amount)).orElse(false);
    }
}
