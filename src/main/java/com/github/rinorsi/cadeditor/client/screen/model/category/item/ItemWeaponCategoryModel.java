package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.FloatEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.LabeledEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringWithActionsEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.SoundEventSelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwingAnimationType;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.KineticWeapon;
import net.minecraft.world.item.component.PiercingWeapon;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.item.component.Weapon;


public class ItemWeaponCategoryModel extends ItemEditorCategoryModel {
    private BooleanEntryModel weaponToggleEntry;
    private IntegerEntryModel weaponDamageEntry;
    private FloatEntryModel weaponDisableEntry;
    private boolean weaponEnabled;
    private int weaponDamagePerAttack;
    private float weaponDisableSeconds;
    private BooleanEntryModel minimumAttackChargeToggleEntry;
    private FloatEntryModel minimumAttackChargeValueEntry;
    private boolean minimumAttackChargeEnabled;
    private float minimumAttackCharge;
    private BooleanEntryModel directDamageTypeToggleEntry;
    private StringWithActionsEntryModel directDamageTypeEntry;
    private boolean directDamageTypeEnabled;
    private String directDamageTypeId;
    private BooleanEntryModel attackRangeToggleEntry;
    private FloatEntryModel attackRangeMinEntry;
    private FloatEntryModel attackRangeMaxEntry;
    private FloatEntryModel attackRangeMinCreativeEntry;
    private FloatEntryModel attackRangeMaxCreativeEntry;
    private FloatEntryModel attackRangeHitboxMarginEntry;
    private FloatEntryModel attackRangeMobFactorEntry;
    private boolean attackRangeEnabled;
    private float attackRangeMin;
    private float attackRangeMax;
    private float attackRangeMinCreative;
    private float attackRangeMaxCreative;
    private float attackRangeHitboxMargin;
    private float attackRangeMobFactor;
    private BooleanEntryModel swingAnimationToggleEntry;
    private StringWithActionsEntryModel swingAnimationTypeEntry;
    private IntegerEntryModel swingAnimationDurationEntry;
    private boolean swingAnimationEnabled;
    private String swingAnimationTypeId;
    private int swingAnimationDuration;
    private BooleanEntryModel piercingWeaponToggleEntry;
    private BooleanEntryModel piercingWeaponDealsKnockbackEntry;
    private BooleanEntryModel piercingWeaponDismountsEntry;
    private SoundEventSelectionEntryModel piercingWeaponSoundEntry;
    private SoundEventSelectionEntryModel piercingWeaponHitSoundEntry;
    private boolean piercingWeaponEnabled;
    private boolean piercingWeaponDealsKnockback;
    private boolean piercingWeaponDismounts;
    private String piercingWeaponSoundId;
    private String piercingWeaponHitSoundId;
    private BooleanEntryModel kineticWeaponToggleEntry;
    private IntegerEntryModel kineticWeaponContactCooldownEntry;
    private IntegerEntryModel kineticWeaponDelayEntry;
    private FloatEntryModel kineticWeaponForwardMovementEntry;
    private FloatEntryModel kineticWeaponDamageMultiplierEntry;
    private SoundEventSelectionEntryModel kineticWeaponSoundEntry;
    private SoundEventSelectionEntryModel kineticWeaponHitSoundEntry;
    private BooleanEntryModel kineticWeaponDismountConditionToggleEntry;
    private IntegerEntryModel kineticWeaponDismountConditionDurationEntry;
    private FloatEntryModel kineticWeaponDismountConditionMinSpeedEntry;
    private FloatEntryModel kineticWeaponDismountConditionMinRelativeSpeedEntry;
    private BooleanEntryModel kineticWeaponKnockbackConditionToggleEntry;
    private IntegerEntryModel kineticWeaponKnockbackConditionDurationEntry;
    private FloatEntryModel kineticWeaponKnockbackConditionMinSpeedEntry;
    private FloatEntryModel kineticWeaponKnockbackConditionMinRelativeSpeedEntry;
    private BooleanEntryModel kineticWeaponDamageConditionToggleEntry;
    private IntegerEntryModel kineticWeaponDamageConditionDurationEntry;
    private FloatEntryModel kineticWeaponDamageConditionMinSpeedEntry;
    private FloatEntryModel kineticWeaponDamageConditionMinRelativeSpeedEntry;
    private boolean kineticWeaponEnabled;
    private int kineticWeaponContactCooldownTicks;
    private int kineticWeaponDelayTicks;
    private float kineticWeaponForwardMovement;
    private float kineticWeaponDamageMultiplier;
    private String kineticWeaponSoundId;
    private String kineticWeaponHitSoundId;
    private boolean kineticWeaponDismountConditionEnabled;
    private int kineticWeaponDismountConditionDuration;
    private float kineticWeaponDismountConditionMinSpeed;
    private float kineticWeaponDismountConditionMinRelativeSpeed;
    private boolean kineticWeaponKnockbackConditionEnabled;
    private int kineticWeaponKnockbackConditionDuration;
    private float kineticWeaponKnockbackConditionMinSpeed;
    private float kineticWeaponKnockbackConditionMinRelativeSpeed;
    private boolean kineticWeaponDamageConditionEnabled;
    private int kineticWeaponDamageConditionDuration;
    private float kineticWeaponDamageConditionMinSpeed;
    private float kineticWeaponDamageConditionMinRelativeSpeed;
    private BooleanEntryModel blocksAttacksToggleEntry;
    private FloatEntryModel blockDelayEntry;
    private FloatEntryModel disableCooldownScaleEntry;
    private StringWithActionsEntryModel bypassedByEntry;
    private StringWithActionsEntryModel damageTypeEntry;
    private FloatEntryModel damageBaseEntry;
    private FloatEntryModel damageFactorEntry;
    private FloatEntryModel damageAngleEntry;
    private FloatEntryModel itemDamageBaseEntry;
    private FloatEntryModel itemDamageFactorEntry;
    private FloatEntryModel itemDamageThresholdEntry;
    private SoundEventSelectionEntryModel blockSoundEntry;
    private SoundEventSelectionEntryModel disableSoundEntry;
    private boolean blocksAttacksEnabled;
    private float blockDelaySeconds;
    private float disableCooldownScale;
    private String bypassedByTag;
    private String damageReductionTypeTag;
    private float damageReductionBase;
    private float damageReductionFactor;
    private float damageReductionAngle;
    private float itemDamageBase;
    private float itemDamageFactor;
    private float itemDamageThreshold;
    private String blockSoundId;
    private String disableSoundId;
    private List<BlocksAttacks.DamageReduction> otherDamageReductions = List.of();

    public ItemWeaponCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("weapon"), editor);
    }

    @Override
    protected void setupEntries() {
        ItemStack stack = getParent().getContext().getItemStack();
        Weapon weapon = stack.get(DataComponents.WEAPON);
        this.weaponEnabled = weapon != null;
        this.weaponDamagePerAttack = weapon != null ? weapon.itemDamagePerAttack() : 1;
        this.weaponDisableSeconds = weapon != null ? weapon.disableBlockingForSeconds() : 0.0f;
        this.weaponToggleEntry = withWikiTooltip(new BooleanEntryModel(this, ModTexts.gui("weapon_enabled"), this.weaponEnabled, value -> setWeaponEnabled(value)), "weapon_enabled", 2);
        getEntries().add(this.weaponToggleEntry);
        this.weaponDamageEntry = withWikiTooltip(new IntegerEntryModel(this, ModTexts.gui("weapon_item_damage"), this.weaponDamagePerAttack, this::setWeaponDamagePerAttack, value -> value != null && value >= 0), "weapon_item_damage", 1);
        this.weaponDisableEntry = withWikiTooltip(new FloatEntryModel(this, ModTexts.gui("weapon_disable_blocking"), this.weaponDisableSeconds, this::setWeaponDisableSeconds, value -> value != null && value >= 0.0f), "weapon_disable_blocking", 1);
        getEntries().add(this.weaponDamageEntry);
        getEntries().add(this.weaponDisableEntry);
        getEntries().add(new SpacerEntryModel(this));
        Float minimumAttackChargeComponent = stack.get(DataComponents.MINIMUM_ATTACK_CHARGE);
        this.minimumAttackChargeEnabled = minimumAttackChargeComponent != null;
        this.minimumAttackCharge = minimumAttackChargeComponent != null ? minimumAttackChargeComponent : 0.0f;
        this.minimumAttackChargeToggleEntry = withWikiTooltip(new BooleanEntryModel(this, ModTexts.gui("minimum_attack_charge_enabled"), this.minimumAttackChargeEnabled, value -> setMinimumAttackChargeEnabled(value)), "minimum_attack_charge_enabled", 2);
        getEntries().add(this.minimumAttackChargeToggleEntry);
        this.minimumAttackChargeValueEntry = withWikiTooltip(new FloatEntryModel(this, ModTexts.gui("minimum_attack_charge_value"), this.minimumAttackCharge, this::setMinimumAttackCharge, value -> value != null && value >= 0.0f && value <= 1.0f), "minimum_attack_charge_value", 2);
        getEntries().add(this.minimumAttackChargeValueEntry);
        getEntries().add(new SpacerEntryModel(this));
        Holder<DamageType> directDamageType = stack.get(DataComponents.DAMAGE_TYPE);
        this.directDamageTypeEnabled = directDamageType != null;
        this.directDamageTypeId = directDamageType != null ? extractDamageTypeId(directDamageType) : "";
        this.directDamageTypeToggleEntry = withWikiTooltip(new BooleanEntryModel(this, ModTexts.gui("damage_type_enabled"), this.directDamageTypeEnabled, value -> setDirectDamageTypeEnabled(value)), "damage_type_enabled", 1);
        getEntries().add(this.directDamageTypeToggleEntry);
        this.directDamageTypeEntry = withWikiTooltip(new StringWithActionsEntryModel(this, ModTexts.gui("damage_type_value"), this.directDamageTypeId, this::setDirectDamageTypeId), "damage_type_value", 1);
        this.directDamageTypeEntry.setPlaceholder("minecraft:player_attack");
        this.directDamageTypeEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_damage_type"), this::openDirectDamageTypeSelection));
        getEntries().add(this.directDamageTypeEntry);
        getEntries().add(new SpacerEntryModel(this));
        AttackRange attackRange = stack.get(DataComponents.ATTACK_RANGE);
        this.attackRangeEnabled = attackRange != null;
        this.attackRangeMin = attackRange != null ? attackRange.minReach() : 0.0f;
        this.attackRangeMax = attackRange != null ? attackRange.maxReach() : 3.0f;
        this.attackRangeMinCreative = attackRange != null ? attackRange.minCreativeReach() : 0.0f;
        this.attackRangeMaxCreative = attackRange != null ? attackRange.maxCreativeReach() : 5.0f;
        this.attackRangeHitboxMargin = attackRange != null ? attackRange.hitboxMargin() : 0.3f;
        this.attackRangeMobFactor = attackRange != null ? attackRange.mobFactor() : 1.0f;
        this.attackRangeToggleEntry = withWikiTooltip(new BooleanEntryModel(this, ModTexts.gui("attack_range_enabled"), this.attackRangeEnabled, value -> setAttackRangeEnabled(value)), "attack_range_enabled", 2);
        getEntries().add(this.attackRangeToggleEntry);
        this.attackRangeMinEntry = withWikiTooltip(new FloatEntryModel(this, ModTexts.gui("attack_range_min"), this.attackRangeMin, this::setAttackRangeMin, value -> value != null && value >= 0.0f && value <= 64.0f), "attack_range_min", 2);
        this.attackRangeMaxEntry = withWikiTooltip(new FloatEntryModel(this, ModTexts.gui("attack_range_max"), this.attackRangeMax, this::setAttackRangeMax, value -> value != null && value >= 0.0f && value <= 64.0f), "attack_range_max", 2);
        this.attackRangeMinCreativeEntry = withWikiTooltip(new FloatEntryModel(this, ModTexts.gui("attack_range_min_creative"), this.attackRangeMinCreative, this::setAttackRangeMinCreative, value -> value != null && value >= 0.0f && value <= 64.0f), "attack_range_min_creative", 2);
        this.attackRangeMaxCreativeEntry = withWikiTooltip(new FloatEntryModel(this, ModTexts.gui("attack_range_max_creative"), this.attackRangeMaxCreative, this::setAttackRangeMaxCreative, value -> value != null && value >= 0.0f && value <= 64.0f), "attack_range_max_creative", 2);
        this.attackRangeHitboxMarginEntry = withWikiTooltip(new FloatEntryModel(this, ModTexts.gui("attack_range_hitbox_margin"), this.attackRangeHitboxMargin, this::setAttackRangeHitboxMargin, value -> value != null && value >= 0.0f && value <= 1.0f), "attack_range_hitbox_margin", 2);
        this.attackRangeMobFactorEntry = withWikiTooltip(new FloatEntryModel(this, ModTexts.gui("attack_range_mob_factor"), this.attackRangeMobFactor, this::setAttackRangeMobFactor, value -> value != null && value >= 0.0f && value <= 2.0f), "attack_range_mob_factor", 2);
        getEntries().add(this.attackRangeMinEntry);
        getEntries().add(this.attackRangeMaxEntry);
        getEntries().add(this.attackRangeMinCreativeEntry);
        getEntries().add(this.attackRangeMaxCreativeEntry);
        getEntries().add(this.attackRangeHitboxMarginEntry);
        getEntries().add(this.attackRangeMobFactorEntry);
        getEntries().add(new SpacerEntryModel(this));
        SwingAnimation swingAnimation = stack.get(DataComponents.SWING_ANIMATION);
        this.swingAnimationEnabled = swingAnimation != null;
        this.swingAnimationTypeId = swingAnimation != null ? swingAnimation.type().getSerializedName() : SwingAnimation.DEFAULT.type().getSerializedName();
        this.swingAnimationDuration = swingAnimation != null ? swingAnimation.duration() : SwingAnimation.DEFAULT.duration();
        this.swingAnimationToggleEntry = new BooleanEntryModel(this, ModTexts.gui("swing_animation_enabled"), this.swingAnimationEnabled, value -> setSwingAnimationEnabled(value));
        getEntries().add(this.swingAnimationToggleEntry);
        this.swingAnimationTypeEntry = new StringWithActionsEntryModel(this, ModTexts.gui("swing_animation_type"), this.swingAnimationTypeId, this::setSwingAnimationTypeId);
        this.swingAnimationTypeEntry.setPlaceholder("whack | stab | none");
        this.swingAnimationTypeEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_swing_animation_type"), this::openSwingAnimationTypeSelection));
        this.swingAnimationDurationEntry = new IntegerEntryModel(this, ModTexts.gui("swing_animation_duration"), this.swingAnimationDuration, this::setSwingAnimationDuration, value -> value != null && value >= 1);
        getEntries().add(this.swingAnimationTypeEntry);
        getEntries().add(this.swingAnimationDurationEntry);
        getEntries().add(new SpacerEntryModel(this));
        PiercingWeapon piercingWeapon = stack.get(DataComponents.PIERCING_WEAPON);
        this.piercingWeaponEnabled = piercingWeapon != null;
        this.piercingWeaponDealsKnockback = piercingWeapon != null ? piercingWeapon.dealsKnockback() : true;
        this.piercingWeaponDismounts = piercingWeapon != null && piercingWeapon.dismounts();
        this.piercingWeaponSoundId = piercingWeapon != null && piercingWeapon.sound().isPresent() ? piercingWeapon.sound().get().unwrapKey().map(key -> key.identifier().toString()).orElse("") : "";
        this.piercingWeaponHitSoundId = piercingWeapon != null && piercingWeapon.hitSound().isPresent() ? piercingWeapon.hitSound().get().unwrapKey().map(key -> key.identifier().toString()).orElse("") : "";
        this.piercingWeaponToggleEntry = withWikiTooltip(new BooleanEntryModel(this, ModTexts.gui("piercing_weapon_enabled"), this.piercingWeaponEnabled, value -> setPiercingWeaponEnabled(value)), "piercing_weapon_enabled", 2);
        getEntries().add(this.piercingWeaponToggleEntry);
        this.piercingWeaponDealsKnockbackEntry = withWikiTooltip(new BooleanEntryModel(this, ModTexts.gui("piercing_weapon_knockback"), this.piercingWeaponDealsKnockback, value -> setPiercingWeaponDealsKnockback(value)), "piercing_weapon_knockback", 1);
        this.piercingWeaponDismountsEntry = withWikiTooltip(new BooleanEntryModel(this, ModTexts.gui("piercing_weapon_dismounts"), this.piercingWeaponDismounts, value -> setPiercingWeaponDismounts(value)), "piercing_weapon_dismounts", 1);
        this.piercingWeaponSoundEntry = withWikiTooltip(new SoundEventSelectionEntryModel(this, ModTexts.gui("piercing_weapon_sound"), this.piercingWeaponSoundId, this::setPiercingWeaponSoundId, namespaceFilter(this.piercingWeaponSoundId)), "piercing_weapon_sound", 1);
        this.piercingWeaponHitSoundEntry = withWikiTooltip(new SoundEventSelectionEntryModel(this, ModTexts.gui("piercing_weapon_hit_sound"), this.piercingWeaponHitSoundId, this::setPiercingWeaponHitSoundId, namespaceFilter(this.piercingWeaponHitSoundId)), "piercing_weapon_hit_sound", 1);
        getEntries().add(this.piercingWeaponDealsKnockbackEntry);
        getEntries().add(this.piercingWeaponDismountsEntry);
        getEntries().add(this.piercingWeaponSoundEntry);
        getEntries().add(this.piercingWeaponHitSoundEntry);
        getEntries().add(new SpacerEntryModel(this));
        KineticWeapon kineticWeapon = stack.get(DataComponents.KINETIC_WEAPON);
        this.kineticWeaponEnabled = kineticWeapon != null;
        this.kineticWeaponContactCooldownTicks = kineticWeapon != null ? kineticWeapon.contactCooldownTicks() : 10;
        this.kineticWeaponDelayTicks = kineticWeapon != null ? kineticWeapon.delayTicks() : 0;
        this.kineticWeaponForwardMovement = kineticWeapon != null ? kineticWeapon.forwardMovement() : 0.0f;
        this.kineticWeaponDamageMultiplier = kineticWeapon != null ? kineticWeapon.damageMultiplier() : 1.0f;
        this.kineticWeaponSoundId = kineticWeapon != null && kineticWeapon.sound().isPresent() ? kineticWeapon.sound().get().unwrapKey().map(key -> key.identifier().toString()).orElse("") : "";
        this.kineticWeaponHitSoundId = kineticWeapon != null && kineticWeapon.hitSound().isPresent() ? kineticWeapon.hitSound().get().unwrapKey().map(key -> key.identifier().toString()).orElse("") : "";
        this.kineticWeaponDismountConditionEnabled = kineticWeapon != null && kineticWeapon.dismountConditions().isPresent();
        this.kineticWeaponKnockbackConditionEnabled = kineticWeapon != null && kineticWeapon.knockbackConditions().isPresent();
        this.kineticWeaponDamageConditionEnabled = kineticWeapon != null && kineticWeapon.damageConditions().isPresent();
        KineticWeapon.Condition dismountCondition = kineticWeapon != null ? (KineticWeapon.Condition) kineticWeapon.dismountConditions().orElse(null) : null;
        KineticWeapon.Condition knockbackCondition = kineticWeapon != null ? (KineticWeapon.Condition) kineticWeapon.knockbackConditions().orElse(null) : null;
        KineticWeapon.Condition damageCondition = kineticWeapon != null ? (KineticWeapon.Condition) kineticWeapon.damageConditions().orElse(null) : null;
        this.kineticWeaponDismountConditionDuration = dismountCondition != null ? dismountCondition.maxDurationTicks() : 0;
        this.kineticWeaponDismountConditionMinSpeed = dismountCondition != null ? dismountCondition.minSpeed() : 0.0f;
        this.kineticWeaponDismountConditionMinRelativeSpeed = dismountCondition != null ? dismountCondition.minRelativeSpeed() : 0.0f;
        this.kineticWeaponKnockbackConditionDuration = knockbackCondition != null ? knockbackCondition.maxDurationTicks() : 0;
        this.kineticWeaponKnockbackConditionMinSpeed = knockbackCondition != null ? knockbackCondition.minSpeed() : 0.0f;
        this.kineticWeaponKnockbackConditionMinRelativeSpeed = knockbackCondition != null ? knockbackCondition.minRelativeSpeed() : 0.0f;
        this.kineticWeaponDamageConditionDuration = damageCondition != null ? damageCondition.maxDurationTicks() : 0;
        this.kineticWeaponDamageConditionMinSpeed = damageCondition != null ? damageCondition.minSpeed() : 0.0f;
        this.kineticWeaponDamageConditionMinRelativeSpeed = damageCondition != null ? damageCondition.minRelativeSpeed() : 0.0f;
        this.kineticWeaponToggleEntry = withWikiTooltip(new BooleanEntryModel(this, ModTexts.gui("kinetic_weapon_enabled"), this.kineticWeaponEnabled, value -> setKineticWeaponEnabled(value)), "kinetic_weapon_enabled", 2);
        getEntries().add(this.kineticWeaponToggleEntry);
        this.kineticWeaponContactCooldownEntry = withWikiTooltip(new IntegerEntryModel(this, ModTexts.gui("kinetic_weapon_contact_cooldown"), this.kineticWeaponContactCooldownTicks, this::setKineticWeaponContactCooldownTicks, value -> value != null && value >= 0), "kinetic_weapon_contact_cooldown", 2);
        this.kineticWeaponDelayEntry = withWikiTooltip(new IntegerEntryModel(this, ModTexts.gui("kinetic_weapon_delay_ticks"), this.kineticWeaponDelayTicks, this::setKineticWeaponDelayTicks, value -> value != null && value >= 0), "kinetic_weapon_delay_ticks", 1);
        this.kineticWeaponForwardMovementEntry = withWikiTooltip(new FloatEntryModel(this, ModTexts.gui("kinetic_weapon_forward_movement"), this.kineticWeaponForwardMovement, this::setKineticWeaponForwardMovement), "kinetic_weapon_forward_movement", 1);
        this.kineticWeaponDamageMultiplierEntry = withWikiTooltip(new FloatEntryModel(this, ModTexts.gui("kinetic_weapon_damage_multiplier"), this.kineticWeaponDamageMultiplier, this::setKineticWeaponDamageMultiplier, value -> value != null && value >= 0.0f), "kinetic_weapon_damage_multiplier", 2);
        this.kineticWeaponSoundEntry = withWikiTooltip(new SoundEventSelectionEntryModel(this, ModTexts.gui("kinetic_weapon_sound"), this.kineticWeaponSoundId, this::setKineticWeaponSoundId, namespaceFilter(this.kineticWeaponSoundId)), "kinetic_weapon_sound", 1);
        this.kineticWeaponHitSoundEntry = withWikiTooltip(new SoundEventSelectionEntryModel(this, ModTexts.gui("kinetic_weapon_hit_sound"), this.kineticWeaponHitSoundId, this::setKineticWeaponHitSoundId, namespaceFilter(this.kineticWeaponHitSoundId)), "kinetic_weapon_hit_sound", 1);
        this.kineticWeaponDismountConditionToggleEntry = withWikiTooltip(new BooleanEntryModel(this, ModTexts.gui("kinetic_weapon_dismount_conditions"), this.kineticWeaponDismountConditionEnabled, value -> setKineticWeaponDismountConditionEnabled(value)), "kinetic_weapon_dismount_conditions", 1);
        this.kineticWeaponDismountConditionDurationEntry = withWikiTooltip(new IntegerEntryModel(this, ModTexts.gui("kinetic_weapon_condition_max_duration"), this.kineticWeaponDismountConditionDuration, this::setKineticWeaponDismountConditionDuration, value -> value != null && value >= 0), "kinetic_weapon_condition_max_duration", 1);
        this.kineticWeaponDismountConditionMinSpeedEntry = withWikiTooltip(new FloatEntryModel(this, ModTexts.gui("kinetic_weapon_condition_min_speed"), this.kineticWeaponDismountConditionMinSpeed, this::setKineticWeaponDismountConditionMinSpeed, value -> value != null && value >= 0.0f), "kinetic_weapon_condition_min_speed", 1);
        this.kineticWeaponDismountConditionMinRelativeSpeedEntry = withWikiTooltip(new FloatEntryModel(this, ModTexts.gui("kinetic_weapon_condition_min_relative_speed"), this.kineticWeaponDismountConditionMinRelativeSpeed, this::setKineticWeaponDismountConditionMinRelativeSpeed, value -> value != null && value >= 0.0f), "kinetic_weapon_condition_min_relative_speed", 1);
        this.kineticWeaponKnockbackConditionToggleEntry = withWikiTooltip(new BooleanEntryModel(this, ModTexts.gui("kinetic_weapon_knockback_conditions"), this.kineticWeaponKnockbackConditionEnabled, value -> setKineticWeaponKnockbackConditionEnabled(value)), "kinetic_weapon_knockback_conditions", 1);
        this.kineticWeaponKnockbackConditionDurationEntry = withWikiTooltip(new IntegerEntryModel(this, ModTexts.gui("kinetic_weapon_condition_max_duration"), this.kineticWeaponKnockbackConditionDuration, this::setKineticWeaponKnockbackConditionDuration, value -> value != null && value >= 0), "kinetic_weapon_condition_max_duration", 1);
        this.kineticWeaponKnockbackConditionMinSpeedEntry = withWikiTooltip(new FloatEntryModel(this, ModTexts.gui("kinetic_weapon_condition_min_speed"), this.kineticWeaponKnockbackConditionMinSpeed, this::setKineticWeaponKnockbackConditionMinSpeed, value -> value != null && value >= 0.0f), "kinetic_weapon_condition_min_speed", 1);
        this.kineticWeaponKnockbackConditionMinRelativeSpeedEntry = withWikiTooltip(new FloatEntryModel(this, ModTexts.gui("kinetic_weapon_condition_min_relative_speed"), this.kineticWeaponKnockbackConditionMinRelativeSpeed, this::setKineticWeaponKnockbackConditionMinRelativeSpeed, value -> value != null && value >= 0.0f), "kinetic_weapon_condition_min_relative_speed", 1);
        this.kineticWeaponDamageConditionToggleEntry = withWikiTooltip(new BooleanEntryModel(this, ModTexts.gui("kinetic_weapon_damage_conditions"), this.kineticWeaponDamageConditionEnabled, value -> setKineticWeaponDamageConditionEnabled(value)), "kinetic_weapon_damage_conditions", 1);
        this.kineticWeaponDamageConditionDurationEntry = withWikiTooltip(new IntegerEntryModel(this, ModTexts.gui("kinetic_weapon_condition_max_duration"), this.kineticWeaponDamageConditionDuration, this::setKineticWeaponDamageConditionDuration, value -> value != null && value >= 0), "kinetic_weapon_condition_max_duration", 1);
        this.kineticWeaponDamageConditionMinSpeedEntry = withWikiTooltip(new FloatEntryModel(this, ModTexts.gui("kinetic_weapon_condition_min_speed"), this.kineticWeaponDamageConditionMinSpeed, this::setKineticWeaponDamageConditionMinSpeed, value -> value != null && value >= 0.0f), "kinetic_weapon_condition_min_speed", 1);
        this.kineticWeaponDamageConditionMinRelativeSpeedEntry = withWikiTooltip(new FloatEntryModel(this, ModTexts.gui("kinetic_weapon_condition_min_relative_speed"), this.kineticWeaponDamageConditionMinRelativeSpeed, this::setKineticWeaponDamageConditionMinRelativeSpeed, value -> value != null && value >= 0.0f), "kinetic_weapon_condition_min_relative_speed", 1);
        getEntries().add(this.kineticWeaponContactCooldownEntry);
        getEntries().add(this.kineticWeaponDelayEntry);
        getEntries().add(this.kineticWeaponForwardMovementEntry);
        getEntries().add(this.kineticWeaponDamageMultiplierEntry);
        getEntries().add(this.kineticWeaponSoundEntry);
        getEntries().add(this.kineticWeaponHitSoundEntry);
        getEntries().add(this.kineticWeaponDismountConditionToggleEntry);
        getEntries().add(this.kineticWeaponDismountConditionDurationEntry);
        getEntries().add(this.kineticWeaponDismountConditionMinSpeedEntry);
        getEntries().add(this.kineticWeaponDismountConditionMinRelativeSpeedEntry);
        getEntries().add(this.kineticWeaponKnockbackConditionToggleEntry);
        getEntries().add(this.kineticWeaponKnockbackConditionDurationEntry);
        getEntries().add(this.kineticWeaponKnockbackConditionMinSpeedEntry);
        getEntries().add(this.kineticWeaponKnockbackConditionMinRelativeSpeedEntry);
        getEntries().add(this.kineticWeaponDamageConditionToggleEntry);
        getEntries().add(this.kineticWeaponDamageConditionDurationEntry);
        getEntries().add(this.kineticWeaponDamageConditionMinSpeedEntry);
        getEntries().add(this.kineticWeaponDamageConditionMinRelativeSpeedEntry);
        getEntries().add(new SpacerEntryModel(this));
        BlocksAttacks blocksAttacks = stack.get(DataComponents.BLOCKS_ATTACKS);
        this.blocksAttacksEnabled = blocksAttacks != null;
        this.blockDelaySeconds = blocksAttacks != null ? blocksAttacks.blockDelaySeconds() : 0.0f;
        this.disableCooldownScale = blocksAttacks != null ? blocksAttacks.disableCooldownScale() : 1.0f;
        if (blocksAttacks != null && blocksAttacks.bypassedBy().isPresent() && blocksAttacks.bypassedBy().get() instanceof HolderSet.Named<DamageType> named) {
            this.bypassedByTag = "#" + named.key().location();
        } else {
            this.bypassedByTag = "";
        }
        List<BlocksAttacks.DamageReduction> reductions = blocksAttacks != null ? blocksAttacks.damageReductions() : List.of();
        BlocksAttacks.DamageReduction reduction = reductions.isEmpty() ? null : reductions.get(0);
        if (reductions.size() > 1) {
            this.otherDamageReductions = List.copyOf(reductions.subList(1, reductions.size()));
        } else {
            this.otherDamageReductions = List.of();
        }
        this.damageReductionAngle = reduction != null ? reduction.horizontalBlockingAngle() : 90.0f;
        this.damageReductionBase = reduction != null ? reduction.base() : 0.0f;
        this.damageReductionFactor = reduction != null ? reduction.factor() : 0.0f;
        this.damageReductionTypeTag = "";
        if (reduction != null && reduction.type().orElse(null) instanceof HolderSet.Named<DamageType> named) {
            this.damageReductionTypeTag = "#" + named.key().location();
        } else if (reduction != null && reduction.type().orElse(null) instanceof HolderSet<DamageType> direct) {
            this.damageReductionTypeTag = direct.stream()
                    .map(holder -> holder.unwrapKey().map(key -> key.identifier().toString()).orElse(""))
                    .filter(id -> !id.isBlank())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
        }
        BlocksAttacks.ItemDamageFunction itemDamageFunction = blocksAttacks != null ? blocksAttacks.itemDamage() : null;
        this.itemDamageThreshold = itemDamageFunction != null ? itemDamageFunction.threshold() : 0.0f;
        this.itemDamageBase = itemDamageFunction != null ? itemDamageFunction.base() : 1.0f;
        this.itemDamageFactor = itemDamageFunction != null ? itemDamageFunction.factor() : 0.0f;
        this.blockSoundId = blocksAttacks != null && blocksAttacks.blockSound().isPresent() ? blocksAttacks.blockSound().get().unwrapKey().map(holder -> holder.identifier().toString()).orElse("") : "";
        this.disableSoundId = blocksAttacks != null && blocksAttacks.disableSound().isPresent() ? blocksAttacks.disableSound().get().unwrapKey().map(holder -> holder.identifier().toString()).orElse("") : "";
        this.blocksAttacksToggleEntry = new BooleanEntryModel(this, ModTexts.gui("blocks_attacks_enabled"), this.blocksAttacksEnabled, value -> setBlocksAttacksEnabled(value));
        getEntries().add(this.blocksAttacksToggleEntry);
        this.blockDelayEntry = new FloatEntryModel(this, ModTexts.gui("blocks_attacks_block_delay"), this.blockDelaySeconds, this::setBlockDelaySeconds, value -> value != null && value >= 0.0f);
        this.disableCooldownScaleEntry = new FloatEntryModel(this, ModTexts.gui("blocks_attacks_disable_scale"), this.disableCooldownScale, this::setDisableCooldownScale, value -> value != null && value >= 0.0f);
        this.bypassedByEntry = new StringWithActionsEntryModel(this, ModTexts.gui("blocks_attacks_bypassed_by"), this.bypassedByTag, this::setBypassedByTag);
        this.bypassedByEntry.setPlaceholder("#minecraft:bypasses_shield");
        this.bypassedByEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_tag"), this::openBypassedTagSelection));
        this.damageTypeEntry = new StringWithActionsEntryModel(this, ModTexts.gui("blocks_attacks_damage_type"), this.damageReductionTypeTag, this::setDamageReductionTypeTag);
        this.damageTypeEntry.setPlaceholder("#minecraft:is_projectile");
        this.damageTypeEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_tag"), this::openDamageTypeTagSelection));
        this.damageBaseEntry = new FloatEntryModel(this, ModTexts.gui("blocks_attacks_damage_base"), this.damageReductionBase, this::setDamageReductionBase);
        this.damageFactorEntry = new FloatEntryModel(this, ModTexts.gui("blocks_attacks_damage_factor"), this.damageReductionFactor, this::setDamageReductionFactor);
        this.damageAngleEntry = new FloatEntryModel(this, ModTexts.gui("blocks_attacks_damage_angle"), this.damageReductionAngle, this::setDamageReductionAngle, value -> value != null && value > 0.0f);
        this.itemDamageBaseEntry = new FloatEntryModel(this, ModTexts.gui("blocks_attacks_item_damage_base"), this.itemDamageBase, this::setItemDamageBase);
        this.itemDamageFactorEntry = new FloatEntryModel(this, ModTexts.gui("blocks_attacks_item_damage_factor"), this.itemDamageFactor, this::setItemDamageFactor);
        this.itemDamageThresholdEntry = new FloatEntryModel(this, ModTexts.gui("blocks_attacks_item_damage_threshold"), this.itemDamageThreshold, this::setItemDamageThreshold, value -> value != null && value >= 0.0f);
        this.blockSoundEntry = new SoundEventSelectionEntryModel(this, ModTexts.gui("blocks_attacks_block_sound"), this.blockSoundId, this::setBlockSoundId, namespaceFilter(this.blockSoundId));
        this.disableSoundEntry = new SoundEventSelectionEntryModel(this, ModTexts.gui("blocks_attacks_disabled_sound"), this.disableSoundId, this::setDisableSoundId, namespaceFilter(this.disableSoundId));
        getEntries().add(this.blockDelayEntry);
        getEntries().add(this.disableCooldownScaleEntry);
        getEntries().add(this.bypassedByEntry);
        getEntries().add(this.damageTypeEntry);
        getEntries().add(this.damageBaseEntry);
        getEntries().add(this.damageFactorEntry);
        getEntries().add(this.damageAngleEntry);
        getEntries().add(this.blockSoundEntry);
        getEntries().add(this.disableSoundEntry);
        getEntries().add(this.itemDamageBaseEntry);
        getEntries().add(this.itemDamageFactorEntry);
        getEntries().add(this.itemDamageThresholdEntry);
        syncWeaponEntriesEnabled();
        syncMinimumAttackChargeEntriesEnabled();
        syncDirectDamageTypeEntriesEnabled();
        syncAttackRangeEntriesEnabled();
        syncSwingAnimationEntriesEnabled();
        syncPiercingWeaponEntriesEnabled();
        syncKineticWeaponEntriesEnabled();
        syncBlocksAttacksEntriesEnabled();
    }

    private void setWeaponEnabled(boolean value) {
        this.weaponEnabled = value;
        syncWeaponEntriesEnabled();
        applyWeaponComponent();
    }

    private void setWeaponDamagePerAttack(Integer value) {
        this.weaponDamagePerAttack = value == null ? 1 : Math.max(0, value);
        if (this.weaponEnabled) {
            applyWeaponComponent();
        }
    }

    private void setWeaponDisableSeconds(Float value) {
        this.weaponDisableSeconds = value == null ? 0.0f : Math.max(0.0f, value);
        if (this.weaponEnabled) {
            applyWeaponComponent();
        }
    }

    private void setMinimumAttackChargeEnabled(boolean value) {
        this.minimumAttackChargeEnabled = value;
        syncMinimumAttackChargeEntriesEnabled();
        applyMinimumAttackChargeComponent();
    }

    private void setMinimumAttackCharge(Float value) {
        this.minimumAttackCharge = value == null ? 0.0f : value;
        if (this.minimumAttackChargeEnabled) {
            applyMinimumAttackChargeComponent();
        }
    }

    private void setDirectDamageTypeEnabled(boolean value) {
        this.directDamageTypeEnabled = value;
        syncDirectDamageTypeEntriesEnabled();
        applyDirectDamageTypeComponent();
    }

    private void setDirectDamageTypeId(String value) {
        this.directDamageTypeId = value == null ? "" : value.trim();
        if (this.directDamageTypeEnabled) {
            applyDirectDamageTypeComponent();
        }
    }

    private void setAttackRangeEnabled(boolean value) {
        this.attackRangeEnabled = value;
        syncAttackRangeEntriesEnabled();
        applyAttackRangeComponent();
    }

    private void setAttackRangeMin(Float value) {
        this.attackRangeMin = value == null ? 0.0f : value;
        if (this.attackRangeEnabled) {
            applyAttackRangeComponent();
        }
    }

    private void setAttackRangeMax(Float value) {
        this.attackRangeMax = value == null ? 3.0f : value;
        if (this.attackRangeEnabled) {
            applyAttackRangeComponent();
        }
    }

    private void setAttackRangeMinCreative(Float value) {
        this.attackRangeMinCreative = value == null ? 0.0f : value;
        if (this.attackRangeEnabled) {
            applyAttackRangeComponent();
        }
    }

    private void setAttackRangeMaxCreative(Float value) {
        this.attackRangeMaxCreative = value == null ? 5.0f : value;
        if (this.attackRangeEnabled) {
            applyAttackRangeComponent();
        }
    }

    private void setAttackRangeHitboxMargin(Float value) {
        this.attackRangeHitboxMargin = value == null ? 0.3f : value;
        if (this.attackRangeEnabled) {
            applyAttackRangeComponent();
        }
    }

    private void setAttackRangeMobFactor(Float value) {
        this.attackRangeMobFactor = value == null ? 1.0f : value;
        if (this.attackRangeEnabled) {
            applyAttackRangeComponent();
        }
    }

    private void setSwingAnimationEnabled(boolean value) {
        this.swingAnimationEnabled = value;
        syncSwingAnimationEntriesEnabled();
        applySwingAnimationComponent();
    }

    private void setSwingAnimationTypeId(String value) {
        this.swingAnimationTypeId = value == null ? "" : value.trim();
        if (this.swingAnimationEnabled) {
            applySwingAnimationComponent();
        }
    }

    private void setSwingAnimationDuration(Integer value) {
        this.swingAnimationDuration = value == null ? SwingAnimation.DEFAULT.duration() : Math.max(1, value);
        if (this.swingAnimationEnabled) {
            applySwingAnimationComponent();
        }
    }

    private void setPiercingWeaponEnabled(boolean value) {
        this.piercingWeaponEnabled = value;
        syncPiercingWeaponEntriesEnabled();
        applyPiercingWeaponComponent();
    }

    private void setPiercingWeaponDealsKnockback(boolean value) {
        this.piercingWeaponDealsKnockback = value;
        if (this.piercingWeaponEnabled) {
            applyPiercingWeaponComponent();
        }
    }

    private void setPiercingWeaponDismounts(boolean value) {
        this.piercingWeaponDismounts = value;
        if (this.piercingWeaponEnabled) {
            applyPiercingWeaponComponent();
        }
    }

    private void setPiercingWeaponSoundId(String id) {
        this.piercingWeaponSoundId = sanitizeId(id);
        if (this.piercingWeaponSoundEntry != null) {
            this.piercingWeaponSoundEntry.setValid(this.piercingWeaponSoundId.isBlank() || resolveSoundHolder(this.piercingWeaponSoundId).isPresent());
        }
        if (this.piercingWeaponEnabled) {
            applyPiercingWeaponComponent();
        }
    }

    private void setPiercingWeaponHitSoundId(String id) {
        this.piercingWeaponHitSoundId = sanitizeId(id);
        if (this.piercingWeaponHitSoundEntry != null) {
            this.piercingWeaponHitSoundEntry.setValid(this.piercingWeaponHitSoundId.isBlank() || resolveSoundHolder(this.piercingWeaponHitSoundId).isPresent());
        }
        if (this.piercingWeaponEnabled) {
            applyPiercingWeaponComponent();
        }
    }

    private void setKineticWeaponEnabled(boolean value) {
        this.kineticWeaponEnabled = value;
        syncKineticWeaponEntriesEnabled();
        applyKineticWeaponComponent();
    }

    private void setKineticWeaponContactCooldownTicks(Integer value) {
        this.kineticWeaponContactCooldownTicks = value == null ? 10 : Math.max(0, value);
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponDelayTicks(Integer value) {
        this.kineticWeaponDelayTicks = value == null ? 0 : Math.max(0, value);
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponForwardMovement(Float value) {
        this.kineticWeaponForwardMovement = value == null ? 0.0f : value;
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponDamageMultiplier(Float value) {
        this.kineticWeaponDamageMultiplier = value == null ? 1.0f : value;
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponSoundId(String id) {
        this.kineticWeaponSoundId = sanitizeId(id);
        if (this.kineticWeaponSoundEntry != null) {
            this.kineticWeaponSoundEntry.setValid(this.kineticWeaponSoundId.isBlank() || resolveSoundHolder(this.kineticWeaponSoundId).isPresent());
        }
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponHitSoundId(String id) {
        this.kineticWeaponHitSoundId = sanitizeId(id);
        if (this.kineticWeaponHitSoundEntry != null) {
            this.kineticWeaponHitSoundEntry.setValid(this.kineticWeaponHitSoundId.isBlank() || resolveSoundHolder(this.kineticWeaponHitSoundId).isPresent());
        }
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponDismountConditionEnabled(boolean value) {
        this.kineticWeaponDismountConditionEnabled = value;
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponDismountConditionDuration(Integer value) {
        this.kineticWeaponDismountConditionDuration = value == null ? 0 : Math.max(0, value);
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponDismountConditionMinSpeed(Float value) {
        this.kineticWeaponDismountConditionMinSpeed = value == null ? 0.0f : Math.max(0.0f, value);
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponDismountConditionMinRelativeSpeed(Float value) {
        this.kineticWeaponDismountConditionMinRelativeSpeed = value == null ? 0.0f : Math.max(0.0f, value);
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponKnockbackConditionEnabled(boolean value) {
        this.kineticWeaponKnockbackConditionEnabled = value;
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponKnockbackConditionDuration(Integer value) {
        this.kineticWeaponKnockbackConditionDuration = value == null ? 0 : Math.max(0, value);
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponKnockbackConditionMinSpeed(Float value) {
        this.kineticWeaponKnockbackConditionMinSpeed = value == null ? 0.0f : Math.max(0.0f, value);
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponKnockbackConditionMinRelativeSpeed(Float value) {
        this.kineticWeaponKnockbackConditionMinRelativeSpeed = value == null ? 0.0f : Math.max(0.0f, value);
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponDamageConditionEnabled(boolean value) {
        this.kineticWeaponDamageConditionEnabled = value;
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponDamageConditionDuration(Integer value) {
        this.kineticWeaponDamageConditionDuration = value == null ? 0 : Math.max(0, value);
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponDamageConditionMinSpeed(Float value) {
        this.kineticWeaponDamageConditionMinSpeed = value == null ? 0.0f : Math.max(0.0f, value);
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setKineticWeaponDamageConditionMinRelativeSpeed(Float value) {
        this.kineticWeaponDamageConditionMinRelativeSpeed = value == null ? 0.0f : Math.max(0.0f, value);
        if (this.kineticWeaponEnabled) {
            applyKineticWeaponComponent();
        }
    }

    private void setBlocksAttacksEnabled(boolean value) {
        this.blocksAttacksEnabled = value;
        syncBlocksAttacksEntriesEnabled();
        applyBlocksAttacksComponent();
    }

    private void setBlockDelaySeconds(Float value) {
        this.blockDelaySeconds = value == null ? 0.0f : Math.max(0.0f, value);
        if (this.blocksAttacksEnabled) {
            applyBlocksAttacksComponent();
        }
    }

    private void setDisableCooldownScale(Float value) {
        this.disableCooldownScale = value == null ? 1.0f : Math.max(0.0f, value);
        if (this.blocksAttacksEnabled) {
            applyBlocksAttacksComponent();
        }
    }

    private void setBypassedByTag(String value) {
        this.bypassedByTag = value == null ? "" : value.trim();
        if (this.blocksAttacksEnabled) {
            applyBlocksAttacksComponent();
        }
    }

    private void setDamageReductionTypeTag(String value) {
        this.damageReductionTypeTag = value == null ? "" : value.trim();
        if (this.blocksAttacksEnabled) {
            applyBlocksAttacksComponent();
        }
    }

    private void setDamageReductionBase(Float value) {
        this.damageReductionBase = value == null ? 0.0f : value;
        if (this.blocksAttacksEnabled) {
            applyBlocksAttacksComponent();
        }
    }

    private void setDamageReductionFactor(Float value) {
        this.damageReductionFactor = value == null ? 0.0f : value;
        if (this.blocksAttacksEnabled) {
            applyBlocksAttacksComponent();
        }
    }

    private void setDamageReductionAngle(Float value) {
        this.damageReductionAngle = value == null ? 90.0f : Math.max(0.1f, value);
        if (this.blocksAttacksEnabled) {
            applyBlocksAttacksComponent();
        }
    }

    private void setItemDamageBase(Float value) {
        this.itemDamageBase = value == null ? 1.0f : value;
        if (this.blocksAttacksEnabled) {
            applyBlocksAttacksComponent();
        }
    }

    private void setItemDamageFactor(Float value) {
        this.itemDamageFactor = value == null ? 0.0f : value;
        if (this.blocksAttacksEnabled) {
            applyBlocksAttacksComponent();
        }
    }

    private void setItemDamageThreshold(Float value) {
        this.itemDamageThreshold = value == null ? 0.0f : Math.max(0.0f, value);
        if (this.blocksAttacksEnabled) {
            applyBlocksAttacksComponent();
        }
    }

    private void setBlockSoundId(String id) {
        this.blockSoundId = sanitizeId(id);
        if (this.blockSoundEntry != null) {
            this.blockSoundEntry.setValid(this.blockSoundId.isBlank() || resolveSoundHolder(this.blockSoundId).isPresent());
        }
        if (this.blocksAttacksEnabled) {
            applyBlocksAttacksComponent();
        }
    }

    private void setDisableSoundId(String id) {
        this.disableSoundId = sanitizeId(id);
        if (this.disableSoundEntry != null) {
            this.disableSoundEntry.setValid(this.disableSoundId.isBlank() || resolveSoundHolder(this.disableSoundId).isPresent());
        }
        if (this.blocksAttacksEnabled) {
            applyBlocksAttacksComponent();
        }
    }

    private void openDirectDamageTypeSelection() {
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_damage_type"), "damage_type", ClientCache.getDamageTypeSelectionItems(), value -> {
            if (value == null || value.isBlank()) {
                return;
            }
            this.directDamageTypeId = value;
            this.directDamageTypeEntry.setValue(value);
            if (this.directDamageTypeEnabled) {
                applyDirectDamageTypeComponent();
            }
        });
    }

    private void openSwingAnimationTypeSelection() {
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_swing_animation_type"), "swing_animation_type", swingAnimationTypeSelectionItems(), value -> {
            if (value == null || value.isBlank()) {
                return;
            }
            Identifier id = tryParse(value);
            String normalized = id != null ? id.getPath() : value;
            this.swingAnimationTypeId = normalized;
            this.swingAnimationTypeEntry.setValue(normalized);
            if (this.swingAnimationEnabled) {
                applySwingAnimationComponent();
            }
        });
    }

    private void openBypassedTagSelection() {
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_tag"), "blocks_attacks_bypass", ClientCache.getDamageTypeTagSelectionItems(), value -> {
            if (value == null || value.isBlank()) {
                return;
            }
            this.bypassedByTag = "#" + value;
            this.bypassedByEntry.setValue(this.bypassedByTag);
            if (this.blocksAttacksEnabled) {
                applyBlocksAttacksComponent();
            }
        });
    }

    private void openDamageTypeTagSelection() {
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_tag"), "blocks_attacks_damage_type", ClientCache.getDamageTypeTagSelectionItems(), value -> {
            if (value == null || value.isBlank()) {
                return;
            }
            this.damageReductionTypeTag = "#" + value;
            this.damageTypeEntry.setValue(this.damageReductionTypeTag);
            if (this.blocksAttacksEnabled) {
                applyBlocksAttacksComponent();
            }
        });
    }

    private void applyWeaponComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.weaponEnabled) {
            stack.remove(DataComponents.WEAPON);
            getParent().removeComponentFromDataTag("minecraft:weapon");
        } else {
            int damage = Math.max(0, this.weaponDamagePerAttack);
            float disable = Math.max(0.0f, this.weaponDisableSeconds);
            stack.set(DataComponents.WEAPON, new Weapon(damage, disable));
        }
    }

    private void applyMinimumAttackChargeComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.minimumAttackChargeEnabled) {
            stack.remove(DataComponents.MINIMUM_ATTACK_CHARGE);
            getParent().removeComponentFromDataTag("minecraft:minimum_attack_charge");
            if (this.minimumAttackChargeValueEntry != null) {
                this.minimumAttackChargeValueEntry.setValid(true);
                return;
            }
            return;
        }
        float clamped = Math.max(0.0f, Math.min(1.0f, this.minimumAttackCharge));
        if (this.minimumAttackChargeValueEntry != null) {
            this.minimumAttackChargeValueEntry.setValid(this.minimumAttackCharge >= 0.0f && this.minimumAttackCharge <= 1.0f);
        }
        stack.set(DataComponents.MINIMUM_ATTACK_CHARGE, clamped);
    }

    private void applyDirectDamageTypeComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.directDamageTypeEnabled || sanitizeId(this.directDamageTypeId).isBlank()) {
            stack.remove(DataComponents.DAMAGE_TYPE);
            getParent().removeComponentFromDataTag("minecraft:damage_type");
            if (this.directDamageTypeEntry != null) {
                this.directDamageTypeEntry.setValid(true);
                return;
            }
            return;
        }
        Identifier parsed = tryParse(this.directDamageTypeId);
        if (parsed == null) {
            if (this.directDamageTypeEntry != null) {
                this.directDamageTypeEntry.setValid(false);
                return;
            }
            return;
        }
        ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, parsed);
        Optional<? extends HolderLookup.RegistryLookup<DamageType>> lookupOpt = ClientUtil.registryAccess().lookup(Registries.DAMAGE_TYPE);
        if (lookupOpt.isEmpty() || ((HolderLookup.RegistryLookup) lookupOpt.get()).get(key).isEmpty()) {
            if (this.directDamageTypeEntry != null) {
                this.directDamageTypeEntry.setValid(false);
            }
        } else {
            if (this.directDamageTypeEntry != null) {
                this.directDamageTypeEntry.setValid(true);
            }
            stack.set(DataComponents.DAMAGE_TYPE, (Holder) ((HolderLookup.RegistryLookup) lookupOpt.get()).get(key).get());
        }
    }

    private void applyAttackRangeComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.attackRangeEnabled) {
            stack.remove(DataComponents.ATTACK_RANGE);
            getParent().removeComponentFromDataTag("minecraft:attack_range");
            markAttackRangeEntriesValid(true);
        } else {
            boolean boundsValid = this.attackRangeMin <= this.attackRangeMax && this.attackRangeMinCreative <= this.attackRangeMaxCreative;
            markAttackRangeEntriesValid(boundsValid);
            if (!boundsValid) {
                return;
            }
            stack.set(DataComponents.ATTACK_RANGE, new AttackRange(clamp(this.attackRangeMin, 0.0f, 64.0f), clamp(this.attackRangeMax, 0.0f, 64.0f), clamp(this.attackRangeMinCreative, 0.0f, 64.0f), clamp(this.attackRangeMaxCreative, 0.0f, 64.0f), clamp(this.attackRangeHitboxMargin, 0.0f, 1.0f), clamp(this.attackRangeMobFactor, 0.0f, 2.0f)));
        }
    }

    private void applySwingAnimationComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.swingAnimationEnabled) {
            stack.remove(DataComponents.SWING_ANIMATION);
            getParent().removeComponentFromDataTag("minecraft:swing_animation");
            if (this.swingAnimationTypeEntry != null) {
                this.swingAnimationTypeEntry.setValid(true);
                return;
            }
            return;
        }
        Optional<SwingAnimationType> parsedType = parseSwingAnimationType(this.swingAnimationTypeId);
        if (parsedType.isEmpty()) {
            if (this.swingAnimationTypeEntry != null) {
                this.swingAnimationTypeEntry.setValid(false);
            }
        } else {
            if (this.swingAnimationTypeEntry != null) {
                this.swingAnimationTypeEntry.setValid(true);
            }
            stack.set(DataComponents.SWING_ANIMATION, new SwingAnimation(parsedType.get(), Math.max(1, this.swingAnimationDuration)));
        }
    }

    private void applyPiercingWeaponComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.piercingWeaponEnabled) {
            stack.remove(DataComponents.PIERCING_WEAPON);
            getParent().removeComponentFromDataTag("minecraft:piercing_weapon");
        } else {
            stack.set(DataComponents.PIERCING_WEAPON, new PiercingWeapon(this.piercingWeaponDealsKnockback, this.piercingWeaponDismounts, resolveSoundHolder(this.piercingWeaponSoundId), resolveSoundHolder(this.piercingWeaponHitSoundId)));
        }
    }

    private void applyKineticWeaponComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.kineticWeaponEnabled) {
            stack.remove(DataComponents.KINETIC_WEAPON);
            getParent().removeComponentFromDataTag("minecraft:kinetic_weapon");
            markKineticConditionEntriesValid(true);
            return;
        }
        Optional<KineticWeapon.Condition> dismountCondition = buildKineticCondition(this.kineticWeaponDismountConditionEnabled, this.kineticWeaponDismountConditionDuration, this.kineticWeaponDismountConditionMinSpeed, this.kineticWeaponDismountConditionMinRelativeSpeed);
        Optional<KineticWeapon.Condition> knockbackCondition = buildKineticCondition(this.kineticWeaponKnockbackConditionEnabled, this.kineticWeaponKnockbackConditionDuration, this.kineticWeaponKnockbackConditionMinSpeed, this.kineticWeaponKnockbackConditionMinRelativeSpeed);
        Optional<KineticWeapon.Condition> damageCondition = buildKineticCondition(this.kineticWeaponDamageConditionEnabled, this.kineticWeaponDamageConditionDuration, this.kineticWeaponDamageConditionMinSpeed, this.kineticWeaponDamageConditionMinRelativeSpeed);
        boolean valid = (!this.kineticWeaponDismountConditionEnabled || dismountCondition.isPresent()) && (!this.kineticWeaponKnockbackConditionEnabled || knockbackCondition.isPresent()) && (!this.kineticWeaponDamageConditionEnabled || damageCondition.isPresent());
        markKineticConditionEntriesValid(valid);
        if (!valid) {
            return;
        }
        stack.set(DataComponents.KINETIC_WEAPON, new KineticWeapon(Math.max(0, this.kineticWeaponContactCooldownTicks), Math.max(0, this.kineticWeaponDelayTicks), dismountCondition, knockbackCondition, damageCondition, this.kineticWeaponForwardMovement, Math.max(0.0f, this.kineticWeaponDamageMultiplier), resolveSoundHolder(this.kineticWeaponSoundId), resolveSoundHolder(this.kineticWeaponHitSoundId)));
    }

    private void applyBlocksAttacksComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.blocksAttacksEnabled) {
            stack.remove(DataComponents.BLOCKS_ATTACKS);
            getParent().removeComponentFromDataTag("minecraft:blocks_attacks");
            return;
        }
        Optional<HolderSet<DamageType>> reductionType;
        boolean typeInvalid = false;
        String typeRaw = sanitizeId(this.damageReductionTypeTag);
        if (typeRaw.isBlank()) {
            reductionType = Optional.empty();
        } else {
            reductionType = parseDamageTypeHolderSetValue(typeRaw);
            typeInvalid = reductionType.isEmpty();
        }
        this.damageTypeEntry.setValid(!typeInvalid);
        if (typeInvalid) {
            return;
        }
        Optional<HolderSet<DamageType>> bypassed;
        boolean bypassInvalid = false;
        String bypassRaw = sanitizeId(this.bypassedByTag);
        if (bypassRaw.isBlank()) {
            bypassed = Optional.empty();
        } else {
            bypassed = parseDamageTypeHolderSetValue(bypassRaw);
            bypassInvalid = bypassed.isEmpty();
        }
        this.bypassedByEntry.setValid(!bypassInvalid);
        if (bypassInvalid) {
            return;
        }
        List<BlocksAttacks.DamageReduction> reductions = new ArrayList<>();
        if (reductionType.isPresent() || this.damageReductionBase != 0.0f || this.damageReductionFactor != 0.0f || this.damageReductionAngle > 0.0f) {
            reductions.add(new BlocksAttacks.DamageReduction(Math.max(0.1f, this.damageReductionAngle), reductionType, this.damageReductionBase, this.damageReductionFactor));
        }
        reductions.addAll(this.otherDamageReductions);
        BlocksAttacks.ItemDamageFunction itemDamage = new BlocksAttacks.ItemDamageFunction(Math.max(0.0f, this.itemDamageThreshold), this.itemDamageBase, this.itemDamageFactor);
        BlocksAttacks component = new BlocksAttacks(Math.max(0.0f, this.blockDelaySeconds), Math.max(0.0f, this.disableCooldownScale), reductions.isEmpty() ? List.of() : List.copyOf(reductions), itemDamage, bypassed, resolveSoundHolder(this.blockSoundId), resolveSoundHolder(this.disableSoundId));
        stack.set(DataComponents.BLOCKS_ATTACKS, component);
    }

    private <T extends LabeledEntryModel> T withWikiTooltip(T entry, String key, int lines) {
        if (entry == null || lines <= 0) {
            return entry;
        }
        MutableComponent[] tooltipLines = ModTexts.wikiTooltip(key, lines);
        for (int i = 0; i < tooltipLines.length; i++) {
            tooltipLines[i] = tooltipLines[i].copy().withStyle(ChatFormatting.GRAY);
        }
        entry.setLabelTooltip(tooltipLines);
        return entry;
    }

    private void syncWeaponEntriesEnabled() {
        this.weaponDamageEntry.setEnabled(this.weaponEnabled);
        this.weaponDisableEntry.setEnabled(this.weaponEnabled);
    }

    private void syncMinimumAttackChargeEntriesEnabled() {
        this.minimumAttackChargeValueEntry.setEnabled(this.minimumAttackChargeEnabled);
    }

    private void syncDirectDamageTypeEntriesEnabled() {
        this.directDamageTypeEntry.setEnabled(this.directDamageTypeEnabled);
    }

    private void syncAttackRangeEntriesEnabled() {
        this.attackRangeMinEntry.setEnabled(this.attackRangeEnabled);
        this.attackRangeMaxEntry.setEnabled(this.attackRangeEnabled);
        this.attackRangeMinCreativeEntry.setEnabled(this.attackRangeEnabled);
        this.attackRangeMaxCreativeEntry.setEnabled(this.attackRangeEnabled);
        this.attackRangeHitboxMarginEntry.setEnabled(this.attackRangeEnabled);
        this.attackRangeMobFactorEntry.setEnabled(this.attackRangeEnabled);
    }

    private void syncSwingAnimationEntriesEnabled() {
        this.swingAnimationTypeEntry.setEnabled(this.swingAnimationEnabled);
        this.swingAnimationDurationEntry.setEnabled(this.swingAnimationEnabled);
    }

    private void syncPiercingWeaponEntriesEnabled() {
        this.piercingWeaponDealsKnockbackEntry.setEnabled(this.piercingWeaponEnabled);
        this.piercingWeaponDismountsEntry.setEnabled(this.piercingWeaponEnabled);
        this.piercingWeaponSoundEntry.setEnabled(this.piercingWeaponEnabled);
        this.piercingWeaponHitSoundEntry.setEnabled(this.piercingWeaponEnabled);
    }

    private void syncKineticWeaponEntriesEnabled() {
        this.kineticWeaponContactCooldownEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponDelayEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponForwardMovementEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponDamageMultiplierEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponSoundEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponHitSoundEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponDismountConditionToggleEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponDismountConditionDurationEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponDismountConditionMinSpeedEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponDismountConditionMinRelativeSpeedEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponKnockbackConditionToggleEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponKnockbackConditionDurationEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponKnockbackConditionMinSpeedEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponKnockbackConditionMinRelativeSpeedEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponDamageConditionToggleEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponDamageConditionDurationEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponDamageConditionMinSpeedEntry.setEnabled(this.kineticWeaponEnabled);
        this.kineticWeaponDamageConditionMinRelativeSpeedEntry.setEnabled(this.kineticWeaponEnabled);
    }

    private void syncBlocksAttacksEntriesEnabled() {
        this.blockDelayEntry.setEnabled(this.blocksAttacksEnabled);
        this.disableCooldownScaleEntry.setEnabled(this.blocksAttacksEnabled);
        this.bypassedByEntry.setEnabled(this.blocksAttacksEnabled);
        this.damageTypeEntry.setEnabled(this.blocksAttacksEnabled);
        this.damageBaseEntry.setEnabled(this.blocksAttacksEnabled);
        this.damageFactorEntry.setEnabled(this.blocksAttacksEnabled);
        this.damageAngleEntry.setEnabled(this.blocksAttacksEnabled);
        this.blockSoundEntry.setEnabled(this.blocksAttacksEnabled);
        this.disableSoundEntry.setEnabled(this.blocksAttacksEnabled);
        this.itemDamageBaseEntry.setEnabled(this.blocksAttacksEnabled);
        this.itemDamageFactorEntry.setEnabled(this.blocksAttacksEnabled);
        this.itemDamageThresholdEntry.setEnabled(this.blocksAttacksEnabled);
    }

    private void markAttackRangeEntriesValid(boolean valid) {
        if (this.attackRangeMinEntry != null) {
            this.attackRangeMinEntry.setValid(valid);
        }
        if (this.attackRangeMaxEntry != null) {
            this.attackRangeMaxEntry.setValid(valid);
        }
        if (this.attackRangeMinCreativeEntry != null) {
            this.attackRangeMinCreativeEntry.setValid(valid);
        }
        if (this.attackRangeMaxCreativeEntry != null) {
            this.attackRangeMaxCreativeEntry.setValid(valid);
        }
        if (this.attackRangeHitboxMarginEntry != null) {
            this.attackRangeHitboxMarginEntry.setValid(valid);
        }
        if (this.attackRangeMobFactorEntry != null) {
            this.attackRangeMobFactorEntry.setValid(valid);
        }
    }

    private void markKineticConditionEntriesValid(boolean valid) {
        setConditionEntriesValid(this.kineticWeaponDismountConditionDurationEntry, this.kineticWeaponDismountConditionMinSpeedEntry, this.kineticWeaponDismountConditionMinRelativeSpeedEntry, !this.kineticWeaponDismountConditionEnabled || valid);
        setConditionEntriesValid(this.kineticWeaponKnockbackConditionDurationEntry, this.kineticWeaponKnockbackConditionMinSpeedEntry, this.kineticWeaponKnockbackConditionMinRelativeSpeedEntry, !this.kineticWeaponKnockbackConditionEnabled || valid);
        setConditionEntriesValid(this.kineticWeaponDamageConditionDurationEntry, this.kineticWeaponDamageConditionMinSpeedEntry, this.kineticWeaponDamageConditionMinRelativeSpeedEntry, !this.kineticWeaponDamageConditionEnabled || valid);
    }

    private void setConditionEntriesValid(IntegerEntryModel durationEntry, FloatEntryModel minSpeedEntry, FloatEntryModel minRelativeEntry, boolean valid) {
        if (durationEntry != null) {
            durationEntry.setValid(valid);
        }
        if (minSpeedEntry != null) {
            minSpeedEntry.setValid(valid);
        }
        if (minRelativeEntry != null) {
            minRelativeEntry.setValid(valid);
        }
    }

    private Optional<KineticWeapon.Condition> buildKineticCondition(boolean enabled, int maxDurationTicks, float minSpeed, float minRelativeSpeed) {
        if (!enabled) {
            return Optional.empty();
        }
        if (maxDurationTicks < 0 || minSpeed < 0.0f || minRelativeSpeed < 0.0f) {
            return Optional.empty();
        }
        return Optional.of(new KineticWeapon.Condition(maxDurationTicks, minSpeed, minRelativeSpeed));
    }

    private List<ListSelectionElementModel> swingAnimationTypeSelectionItems() {
        List<ListSelectionElementModel> items = new ArrayList<>();
        for (SwingAnimationType type : SwingAnimationType.values()) {
            String key = "cadeditor.gui.swing_animation_type." + type.getSerializedName();
            Identifier id = Identifier.withDefaultNamespace(type.getSerializedName());
            items.add(new ListSelectionElementModel(key, id));
        }
        return items;
    }

    private Optional<SwingAnimationType> parseSwingAnimationType(String value) {
        String sanitized = sanitizeId(value).toLowerCase(Locale.ROOT);
        if (sanitized.isBlank()) {
            return Optional.empty();
        }
        if (sanitized.startsWith("minecraft:")) {
            sanitized = sanitized.substring("minecraft:".length());
        }
        for (SwingAnimationType type : SwingAnimationType.values()) {
            if (type.getSerializedName().equals(sanitized) || type.name().equalsIgnoreCase(sanitized)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    private String extractDamageTypeId(Holder<DamageType> holder) {
        return holder.unwrapKey().map(key -> key.identifier().toString()).orElse("");
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private Optional<HolderSet<DamageType>> parseDamageTypeHolderSetValue(String value) {
        String sanitized = sanitizeId(value);
        if (sanitized.isBlank()) {
            return Optional.empty();
        }
        Optional<? extends HolderLookup.RegistryLookup<DamageType>> lookupOpt = ClientUtil.registryAccess().lookup(Registries.DAMAGE_TYPE);
        if (lookupOpt.isEmpty()) {
            return Optional.empty();
        }
        HolderLookup.RegistryLookup<DamageType> lookup = lookupOpt.get();
        if (sanitized.startsWith("#")) {
            Identifier rl = tryParse(sanitized.substring(1));
            if (rl == null) {
                return Optional.empty();
            }
            TagKey<DamageType> tag = TagKey.create(Registries.DAMAGE_TYPE, rl);
            return lookup.get(tag).map(named -> named);
        }
        List<Holder<DamageType>> holders = new ArrayList<>();
        for (String part : sanitized.split("[,\\n]")) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            Identifier rl = tryParse(trimmed);
            if (rl == null) {
                return Optional.empty();
            }
            Optional<Holder.Reference<DamageType>> holder = lookup.get(ResourceKey.create(Registries.DAMAGE_TYPE, rl));
            if (holder.isEmpty()) {
                return Optional.empty();
            }
            holders.add(holder.get());
        }
        if (holders.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(HolderSet.direct(holders));
    }

    private Optional<Holder<SoundEvent>> resolveSoundHolder(String id) {
        return resolveSoundHolder((HolderLookup.RegistryLookup) ClientUtil.registryAccess().lookup(Registries.SOUND_EVENT).orElse(null), id);
    }

    private Optional<Holder<SoundEvent>> resolveSoundHolder(HolderLookup.RegistryLookup<SoundEvent> lookup, String id) {
        if (lookup == null) {
            return Optional.empty();
        }
        String sanitized = sanitizeId(id);
        if (sanitized.isBlank()) {
            return Optional.empty();
        }
        Identifier rl = tryParse(sanitized);
        if (rl == null) {
            return Optional.empty();
        }
        ResourceKey<SoundEvent> key = ResourceKey.create(Registries.SOUND_EVENT, rl);
        return lookup.get(key).map(holder -> holder);
    }

    private String sanitizeId(String id) {
        return id == null ? "" : stripQuotes(id.trim());
    }

    private String stripQuotes(String value) {
        if (value == null) {
            return "";
        }
        String result = value.trim();
        if (((result.startsWith("\"") && result.endsWith("\"")) || (result.startsWith("'") && result.endsWith("'"))) && result.length() >= 2) {
            result = result.substring(1, result.length() - 1);
        }
        return result.trim();
    }

    private Identifier tryParse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String sanitized = value.trim();
        try {
            return Identifier.parse(sanitized);
        } catch (Exception e) {
            if (!sanitized.contains(":")) {
                try {
                    return Identifier.parse("minecraft:" + sanitized);
                } catch (Exception ignored) {
                    return null;
                }
            }
            return null;
        }
    }

    private String namespaceFilter(String id) {
        String sanitized = sanitizeId(id);
        if (sanitized.isBlank()) {
            return null;
        }
        String namespace = sanitized.contains(":") ? sanitized.substring(0, sanitized.indexOf(58)) : "minecraft";
        return "namespace:" + namespace.toLowerCase(Locale.ROOT);
    }
}