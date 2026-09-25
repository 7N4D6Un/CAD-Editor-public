package com.github.rinorsi.cadeditor.common;

import com.github.franckyi.guapi.api.GuapiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemUseAnimation;


public final class ModTexts {
    public static final MutableComponent ADD = GuapiHelper.translated("cadeditor.gui.add").withStyle(ChatFormatting.GREEN);
    public static final MutableComponent AMBIENT = GuapiHelper.translated("cadeditor.gui.ambient");
    public static final MutableComponent AMOUNT = GuapiHelper.translated("cadeditor.gui.amount");
    public static final MutableComponent AMPLIFIER = GuapiHelper.translated("cadeditor.gui.amplifier");
    public static final MutableComponent AQUA = GuapiHelper.translated("cadeditor.gui.aqua").withStyle(ChatFormatting.AQUA);
    public static final MutableComponent ARMOR_COLOR = GuapiHelper.translated("cadeditor.gui.armor_color");
    public static final MutableComponent ATTRIBUTE = GuapiHelper.translated("cadeditor.gui.attribute");
    public static final MutableComponent ATTRIBUTE_MODIFIERS = GuapiHelper.translated("cadeditor.gui.attribute_modifiers");
    public static final MutableComponent ATTRIBUTE_NAME = GuapiHelper.translated("cadeditor.gui.attribute_name");
    public static final MutableComponent BLACK = GuapiHelper.translated("cadeditor.gui.black").withStyle(ChatFormatting.AQUA);
    public static final MutableComponent BLOCK = GuapiHelper.translated("cadeditor.text.block");
    public static final MutableComponent BLUE = GuapiHelper.translated("cadeditor.gui.blue").withStyle(ChatFormatting.BLUE);
    public static final MutableComponent BLUE_COLOR = GuapiHelper.translated("cadeditor.gui.blue");
    public static final MutableComponent BOLD = GuapiHelper.translated("cadeditor.gui.bold");
    public static final MutableComponent CANCEL = GuapiHelper.translated("gui.cancel").withStyle(ChatFormatting.RED);
    public static final MutableComponent CAN_DESTROY = GuapiHelper.translated("cadeditor.gui.can_destroy");
    public static final MutableComponent CAN_PLACE_ON = GuapiHelper.translated("cadeditor.gui.can_place_on");
    public static final MutableComponent CLIENT = GuapiHelper.translated("cadeditor.gui.client");
    public static final MutableComponent CLOSE = GuapiHelper.translated("cadeditor.gui.close_without_saving").withStyle(ChatFormatting.RED);
    public static final MutableComponent COLLAPSE = GuapiHelper.translated("cadeditor.gui.collapse");
    public static final MutableComponent COPY = GuapiHelper.translated("cadeditor.gui.copy");
    public static final MutableComponent COUNT = GuapiHelper.translated("cadeditor.gui.count");
    public static final MutableComponent CUSTOM_COLOR = GuapiHelper.translated("cadeditor.gui.custom_color");
    public static final MutableComponent CUSTOM_NAME = GuapiHelper.translated("cadeditor.gui.custom_name");
    public static final MutableComponent CUT = GuapiHelper.translated("cadeditor.gui.cut");
    public static final MutableComponent DAMAGE = GuapiHelper.translated("cadeditor.gui.damage");
    public static final MutableComponent DARK_AQUA = GuapiHelper.translated("cadeditor.gui.dark_aqua").withStyle(ChatFormatting.AQUA);
    public static final MutableComponent DARK_BLUE = GuapiHelper.translated("cadeditor.gui.dark_blue").withStyle(ChatFormatting.BLUE);
    public static final MutableComponent DARK_GRAY = GuapiHelper.translated("cadeditor.gui.dark_gray").withStyle(ChatFormatting.GRAY);
    public static final MutableComponent DARK_GREEN = GuapiHelper.translated("cadeditor.gui.dark_green").withStyle(ChatFormatting.GREEN);
    public static final MutableComponent DARK_PURPLE = GuapiHelper.translated("cadeditor.gui.dark_purple").withStyle(ChatFormatting.LIGHT_PURPLE);
    public static final MutableComponent DARK_RED = GuapiHelper.translated("cadeditor.gui.dark_red").withStyle(ChatFormatting.RED);
    public static final MutableComponent DEBUG_MODE = GuapiHelper.translated("cadeditor.gui.debug_mode");
    public static final MutableComponent DEFAULT_POTION = GuapiHelper.translated("cadeditor.gui.default_potion");
    public static final MutableComponent DISPLAY = GuapiHelper.translated("cadeditor.gui.display");
    public static final MutableComponent ITEM_FRAME = GuapiHelper.translated("cadeditor.gui.item_frame");
    public static final MutableComponent ITEM_FRAME_ITEM = GuapiHelper.translated("cadeditor.gui.item_frame_item");
    public static final MutableComponent ITEM_FRAME_DROP_CHANCE = GuapiHelper.translated("cadeditor.gui.item_frame_drop_chance");
    public static final MutableComponent ITEM_FRAME_ROTATION = GuapiHelper.translated("cadeditor.gui.item_frame_rotation");
    public static final MutableComponent ITEM_FRAME_FIXED = GuapiHelper.translated("cadeditor.gui.item_frame_fixed");
    public static final MutableComponent ITEM_FRAME_INVISIBLE = GuapiHelper.translated("cadeditor.gui.item_frame_invisible");
    public static final MutableComponent ITEM_FRAME_FACING = GuapiHelper.translated("cadeditor.gui.item_frame_facing");
    public static final MutableComponent DONE = GuapiHelper.translated("gui.done").withStyle(ChatFormatting.GREEN);
    public static final MutableComponent DURATION = GuapiHelper.translated("cadeditor.gui.duration");
    public static final MutableComponent EFFECT = GuapiHelper.translated("cadeditor.gui.effect");
    public static final MutableComponent EFFECTS = GuapiHelper.translated("cadeditor.gui.effect");
    public static final MutableComponent ENCHANTMENT = GuapiHelper.translated("cadeditor.gui.enchantment");
    public static final MutableComponent ENCHANTMENTS = GuapiHelper.translated("cadeditor.gui.enchantments");
    public static final MutableComponent ENTITY = GuapiHelper.translated("cadeditor.text.entity");
    public static final MutableComponent ENTITY_ATTRIBUTES = GuapiHelper.translated("cadeditor.gui.entity_attributes");
    public static final MutableComponent ENTITY_EQUIPMENT = GuapiHelper.translated("cadeditor.gui.entity_equipment");
    public static final MutableComponent ENTITY_SPAWN = GuapiHelper.translated("cadeditor.gui.entity_spawn");
    public static final MutableComponent ENTITY_TAMING = GuapiHelper.translated("cadeditor.gui.entity_taming");
    public static final MutableComponent ENTITY_MOUNT = GuapiHelper.translated("cadeditor.gui.entity_mount");
    public static final MutableComponent TAME = GuapiHelper.translated("cadeditor.gui.tame");
    public static final MutableComponent OWNER_UUID = GuapiHelper.translated("cadeditor.gui.owner_uuid");
    public static final MutableComponent COLLAR_COLOR = GuapiHelper.translated("cadeditor.gui.collar_color");
    public static final MutableComponent AGE_LOCKED = GuapiHelper.translated("cadeditor.gui.age_locked");
    public static final MutableComponent VARIANT = GuapiHelper.translated("cadeditor.gui.variant");
    public static final MutableComponent SITTING = GuapiHelper.translated("cadeditor.gui.sitting");
    public static final MutableComponent SADDLED = GuapiHelper.translated("cadeditor.gui.saddled");
    public static final MutableComponent SADDLE_ITEM = GuapiHelper.translated("cadeditor.gui.saddle_item");
    public static final MutableComponent CHESTED_HORSE = GuapiHelper.translated("cadeditor.gui.chested_horse");
    public static final MutableComponent PASSENGERS = GuapiHelper.translated("cadeditor.gui.passengers");
    public static final MutableComponent LEASH_HOLDER = GuapiHelper.translated("cadeditor.gui.leash_holder");
    public static final MutableComponent LEASH_ANCHOR = GuapiHelper.translated("cadeditor.gui.leash_anchor");
    public static final MutableComponent LEASH_POS_X = GuapiHelper.translated("cadeditor.gui.leash_pos_x");
    public static final MutableComponent LEASH_POS_Y = GuapiHelper.translated("cadeditor.gui.leash_pos_y");
    public static final MutableComponent LEASH_POS_Z = GuapiHelper.translated("cadeditor.gui.leash_pos_z");
    public static final MutableComponent USE_SELF_UUID = GuapiHelper.translated("cadeditor.gui.use_self_uuid").withStyle(ChatFormatting.GREEN);
    public static final MutableComponent MOUNT_TEMPER = GuapiHelper.translated("cadeditor.gui.mount_temper");
    public static final MutableComponent MOUNT_STRENGTH = GuapiHelper.translated("cadeditor.gui.mount_strength");
    public static final MutableComponent VILLAGER_DATA = GuapiHelper.translated("cadeditor.gui.villager_data");
    public static final MutableComponent VILLAGER_PROFESSION = GuapiHelper.translated("cadeditor.gui.villager_profession");
    public static final MutableComponent VILLAGER_TYPE = GuapiHelper.translated("cadeditor.gui.villager_type");
    public static final MutableComponent VILLAGER_LEVEL = GuapiHelper.translated("cadeditor.gui.villager_level");
    public static final MutableComponent VILLAGER_TRADES = GuapiHelper.translated("cadeditor.gui.villager_trades");
    public static final MutableComponent TRADE_INPUT_PRIMARY = GuapiHelper.translated("cadeditor.gui.trade_input_primary");
    public static final MutableComponent TRADE_INPUT_SECONDARY = GuapiHelper.translated("cadeditor.gui.trade_input_secondary");
    public static final MutableComponent TRADE_OUTPUT = GuapiHelper.translated("cadeditor.gui.trade_output");
    public static final MutableComponent TRADE_MAX_USES = GuapiHelper.translated("cadeditor.gui.trade_max_uses");
    public static final MutableComponent TRADE_USES = GuapiHelper.translated("cadeditor.gui.trade_uses");
    public static final MutableComponent TRADE_DEMAND = GuapiHelper.translated("cadeditor.gui.trade_demand");
    public static final MutableComponent TRADE_SPECIAL_PRICE = GuapiHelper.translated("cadeditor.gui.trade_special_price");
    public static final MutableComponent TRADE_PRICE_MULTIPLIER = GuapiHelper.translated("cadeditor.gui.trade_price_multiplier");
    public static final MutableComponent TRADE_REWARD_EXP = GuapiHelper.translated("cadeditor.gui.trade_reward_exp");
    public static final MutableComponent TRADE_XP = GuapiHelper.translated("cadeditor.gui.trade_xp");
    public static final MutableComponent TRADE_ADD = GuapiHelper.translated("cadeditor.gui.trade_add");
    public static final MutableComponent LOOT_TABLE = GuapiHelper.translated("cadeditor.gui.loot_table");
    public static final MutableComponent SEED = GuapiHelper.translated("cadeditor.gui.seed");
    public static final MutableComponent CONTAINER_GRID = GuapiHelper.translated("cadeditor.gui.container_grid");
    public static final MutableComponent EXPAND = GuapiHelper.translated("cadeditor.gui.expand");
    public static final MutableComponent FIX_ERRORS = GuapiHelper.translated("cadeditor.gui.fix_errors").withStyle(ChatFormatting.RED);
    public static final MutableComponent GENERAL = GuapiHelper.translated("cadeditor.gui.general");
    public static final MutableComponent GOLD = GuapiHelper.translated("cadeditor.gui.gold").withStyle(ChatFormatting.YELLOW);
    public static final MutableComponent GRAY = GuapiHelper.translated("cadeditor.gui.gray").withStyle(ChatFormatting.GRAY);
    public static final MutableComponent GREEN = GuapiHelper.translated("cadeditor.gui.green").withStyle(ChatFormatting.GREEN);
    public static final MutableComponent GREEN_COLOR = GuapiHelper.translated("cadeditor.gui.green");
    public static final MutableComponent HIDE_FLAGS = GuapiHelper.translated("cadeditor.gui.hide_flags");
    public static final MutableComponent INTANGIBLE_PROJECTILE = GuapiHelper.translated("cadeditor.gui.intangible_projectile");
    public static final MutableComponent MAX_STACK_SIZE = GuapiHelper.translated("cadeditor.gui.max_stack_size");
    public static final MutableComponent MAX_DAMAGE = GuapiHelper.translated("cadeditor.gui.max_damage");
    public static final MutableComponent[] HIDE_OTHER_TOOLTIP = arrayText("cadeditor.gui.hide_other_tooltip", 8);
    public static final MutableComponent ITALIC = GuapiHelper.translated("cadeditor.gui.italic");
    public static final MutableComponent ITEM = GuapiHelper.translated("cadeditor.text.item");
    public static final MutableComponent ITEM_NAME = GuapiHelper.translated("cadeditor.gui.item_name");
    public static final MutableComponent ITEM_ID = GuapiHelper.translated("cadeditor.gui.item_id");
    public static final MutableComponent LEVEL_ADD = GuapiHelper.translated("cadeditor.gui.level_add", GuapiHelper.text("+1")).withStyle(ChatFormatting.GREEN);
    public static final MutableComponent LEVEL_REMOVE = GuapiHelper.translated("cadeditor.gui.level_add", GuapiHelper.text("-1")).withStyle(ChatFormatting.GREEN);
    public static final MutableComponent LIGHT_PURPLE = GuapiHelper.translated("cadeditor.gui.light_purple").withStyle(ChatFormatting.LIGHT_PURPLE);
    public static final MutableComponent LORE_ADD = GuapiHelper.translated("cadeditor.gui.lore_add");
    public static final MutableComponent MODIFIER = GuapiHelper.translated("cadeditor.gui.modifier");
    public static final MutableComponent MOVE_DOWN = GuapiHelper.translated("cadeditor.gui.move_down");
    public static final MutableComponent MOVE_UP = GuapiHelper.translated("cadeditor.gui.move_up");
    public static final MutableComponent OBFUSCATED = GuapiHelper.translated("cadeditor.gui.obfuscated");
    public static final MutableComponent PASTE = GuapiHelper.translated("cadeditor.gui.paste");
    public static final MutableComponent POTION = GuapiHelper.translated("cadeditor.gui.potion");
    public static final MutableComponent POTION_COLOR = GuapiHelper.translated("cadeditor.gui.potion_color");
    public static final MutableComponent POTION_EFFECTS = GuapiHelper.translated("cadeditor.gui.potion_effects");
    public static final MutableComponent RED = GuapiHelper.translated("cadeditor.gui.red").withStyle(ChatFormatting.RED);
    public static final MutableComponent RED_COLOR = GuapiHelper.translated("cadeditor.gui.red");
    public static final MutableComponent RELOAD_CONFIG = GuapiHelper.translated("cadeditor.gui.reload_config").withStyle(ChatFormatting.YELLOW);
    public static final MutableComponent REMOVE = GuapiHelper.translated("cadeditor.gui.remove").withStyle(ChatFormatting.RED);
    public static final MutableComponent REMOVE_CUSTOM_COLOR = GuapiHelper.translated("cadeditor.gui.remove_custom_color").withStyle(ChatFormatting.RED);
    public static final MutableComponent RESET = GuapiHelper.translated("cadeditor.gui.reset").withStyle(ChatFormatting.YELLOW);
    public static final MutableComponent RESET_COLOR = GuapiHelper.translated("cadeditor.gui.reset_color");
    public static final MutableComponent SAVE = GuapiHelper.translated("cadeditor.gui.save").withStyle(ChatFormatting.GREEN);
    public static final MutableComponent SAVE_EDIT = GuapiHelper.translated("cadeditor.gui.save_edit").withStyle(ChatFormatting.GREEN);
    public static final MutableComponent SCROLL_FOCUSED = GuapiHelper.translated("cadeditor.gui.scroll_focused");
    public static final MutableComponent SEARCH = GuapiHelper.translated("cadeditor.gui.search");
    public static final MutableComponent LOAD_ALL = GuapiHelper.translated("cadeditor.gui.load_all");
    public static final MutableComponent SHOWING_ALL = GuapiHelper.translated("cadeditor.gui.showing_all").withStyle(ChatFormatting.GRAY);
    public static final MutableComponent SETTINGS = GuapiHelper.translated("cadeditor.gui.settings");
    public static final MutableComponent SECONDS = GuapiHelper.translated("cadeditor.gui.seconds");
    public static final MutableComponent SHOW_ICON = GuapiHelper.translated("cadeditor.gui.show_icon");
    public static final MutableComponent SHOW_PARTICLES = GuapiHelper.translated("cadeditor.gui.show_particles");
    public static final MutableComponent SLOT = GuapiHelper.translated("cadeditor.gui.slot");
    public static final MutableComponent DROP_CHANCE = GuapiHelper.translated("cadeditor.gui.drop_chance");
    public static final MutableComponent CHOOSE_ITEM = GuapiHelper.translated("cadeditor.gui.choose_item");
    public static final MutableComponent MAIN_HAND = GuapiHelper.translated("cadeditor.gui.mainhand");
    public static final MutableComponent OFF_HAND = GuapiHelper.translated("cadeditor.gui.offhand");
    public static final MutableComponent HEAD = GuapiHelper.translated("cadeditor.gui.head");
    public static final MutableComponent CHEST = GuapiHelper.translated("cadeditor.gui.chest");
    public static final MutableComponent LEGS = GuapiHelper.translated("cadeditor.gui.legs");
    public static final MutableComponent FEET = GuapiHelper.translated("cadeditor.gui.feet");
    public static final MutableComponent BODY_SLOT = GuapiHelper.translated("cadeditor.gui.body_slot");
    public static final MutableComponent STRIKETHROUGH = GuapiHelper.translated("cadeditor.gui.strikethrough");
    public static final MutableComponent TICKS = GuapiHelper.translated("cadeditor.gui.ticks");
    public static final MutableComponent THEME = GuapiHelper.translated("cadeditor.gui.theme");
    public static final MutableComponent SYNTAX_HIGHLIGHTING_PRESET = GuapiHelper.translated("cadeditor.gui.syntax_highlighting_preset");
    public static final MutableComponent UNBREAKABLE = GuapiHelper.translated("cadeditor.gui.unbreakable");
    public static final MutableComponent UNDERLINED = GuapiHelper.translated("cadeditor.gui.underline");
    public static final MutableComponent WHITE = GuapiHelper.translated("cadeditor.gui.white").withStyle(ChatFormatting.WHITE);
    public static final MutableComponent YELLOW = GuapiHelper.translated("cadeditor.gui.yellow").withStyle(ChatFormatting.YELLOW);
    public static final MutableComponent ZOOM_IN = GuapiHelper.translated("cadeditor.gui.zoom_in");
    public static final MutableComponent ZOOM_OUT = GuapiHelper.translated("cadeditor.gui.zoom_out");
    public static final MutableComponent ZOOM_RESET = GuapiHelper.translated("cadeditor.gui.zoom_reset");
    public static final MutableComponent SELECTION_SCREEN_MAX_ITEMS = GuapiHelper.translated("cadeditor.gui.selection_screen_max_items");
    public static final MutableComponent ATTRIBUTE_TOOLTIP_INFO = GuapiHelper.translated("cadeditor.gui.attribute_tooltip_info").withStyle(ChatFormatting.GRAY);
    public static final MutableComponent LEVEL = GuapiHelper.translated("cadeditor.gui.level");
    public static final MutableComponent COMMON = GuapiHelper.translated("cadeditor.gui.common");
    public static final MutableComponent SNBT_PREVIEW = GuapiHelper.translated("cadeditor.gui.snbt_preview");
    public static final MutableComponent SNBT_PREVIEW_INVALID = GuapiHelper.translated("cadeditor.gui.snbt_preview_invalid").withStyle(ChatFormatting.RED);
    public static final MutableComponent SNBT_PREVIEW_TOGGLE = GuapiHelper.translated("cadeditor.gui.snbt_preview_toggle");
    public static final MutableComponent EQUIPMENT_ASSET = GuapiHelper.translated("cadeditor.gui.equipment_asset");
    public static final MutableComponent PERMISSION_LEVEL = GuapiHelper.translated("cadeditor.gui.permission_level");
    public static final MutableComponent CREATIVE_ONLY = GuapiHelper.translated("cadeditor.gui.creative_only");
    public static final MutableComponent BLOCK_STATE = GuapiHelper.translated("cadeditor.gui.block_state");
    public static final MutableComponent SPAWN_EGG = GuapiHelper.translated("cadeditor.gui.spawn_egg");
    public static final MutableComponent CONTAINER = GuapiHelper.translated("cadeditor.gui.container");
    public static final MutableComponent LOCK_REQUIRED_ITEM = GuapiHelper.translated("cadeditor.gui.lock_required_item");
    public static final MutableComponent LOCK_REQUIRED_COUNT = GuapiHelper.translated("cadeditor.gui.lock_required_count");
    public static final MutableComponent LOCK_PASSWORD = GuapiHelper.translated("cadeditor.gui.unlock_password");
    public static final MutableComponent VAULT = GuapiHelper.translated("cadeditor.text.vault");
    public static final MutableComponent HEALTH = GuapiHelper.translated("cadeditor.gui.health");
    public static final MutableComponent ALWAYS_SHOW_NAME = GuapiHelper.translated("cadeditor.gui.always_show_name");
    public static final MutableComponent INVULNERABLE = GuapiHelper.translated("cadeditor.gui.invulnerable");
    public static final MutableComponent SILENT = GuapiHelper.translated("cadeditor.gui.silent");
    public static final MutableComponent NO_GRAVITY = GuapiHelper.translated("cadeditor.gui.no_gravity");
    public static final MutableComponent GLOWING = GuapiHelper.translated("cadeditor.gui.glowing");
    public static final MutableComponent FIRE = GuapiHelper.translated("cadeditor.gui.fire");
    public static final MutableComponent SAVE_VAULT = GuapiHelper.translated("cadeditor.gui.save_vault");
    public static final MutableComponent LOAD_VAULT = GuapiHelper.translated("cadeditor.gui.load_vault");
    public static final MutableComponent CAN_PICK_UP_LOOT = GuapiHelper.translated("cadeditor.gui.can_pick_up_loot");
    public static final MutableComponent PERSISTENCE_REQUIRED = GuapiHelper.translated("cadeditor.gui.persistence_required");
    public static final MutableComponent NO_AI = GuapiHelper.translated("cadeditor.gui.no_ai");
    public static final MutableComponent LEFT_HANDED = GuapiHelper.translated("cadeditor.gui.left_handed");
    public static final MutableComponent TEAM = GuapiHelper.translated("cadeditor.gui.team");
    public static final MutableComponent CAN_JOIN_RAID = GuapiHelper.translated("cadeditor.gui.can_join_raid");
    public static final MutableComponent PATROL_LEADER = GuapiHelper.translated("cadeditor.gui.patrol_leader");
    public static final MutableComponent SAVE_VAULT_GREEN = GuapiHelper.translated("cadeditor.gui.save_vault").withStyle(ChatFormatting.GREEN);
    public static final MutableComponent OPEN_EDITOR = GuapiHelper.translated("cadeditor.key.editor");
    public static final MutableComponent OPEN_NBT_EDITOR = GuapiHelper.translated("cadeditor.key.nbt_editor");
    public static final MutableComponent OPEN_SNBT_EDITOR = GuapiHelper.translated("cadeditor.key.snbt_editor");
    public static final MutableComponent COPY_COMMAND_GREEN = GuapiHelper.translated("cadeditor.gui.copy_command_alt").withStyle(ChatFormatting.GREEN);
    public static final MutableComponent APPLY_VANILLA_COMMAND = GuapiHelper.translated("cadeditor.gui.apply_vanilla_command");
    public static final MutableComponent APPLY_VANILLA_COMMAND_ACTIVE = GuapiHelper.translated("cadeditor.gui.apply_vanilla_command_active");
    public static final MutableComponent APPLY_VANILLA_COMMAND_WARNING = GuapiHelper.translated("cadeditor.gui.apply_vanilla_command_warning");
    public static final MutableComponent APPLY_VANILLA_COMMAND_GREEN = GuapiHelper.translated("cadeditor.gui.apply_vanilla_command_alt").withStyle(ChatFormatting.GREEN);
    public static final MutableComponent FORMAT = GuapiHelper.translated("cadeditor.gui.format");
    public static final MutableComponent TOOL = GuapiHelper.translated("cadeditor.gui.tool");
    public static final MutableComponent TOOL_MINING_SPEED = GuapiHelper.translated("cadeditor.gui.tool_default_speed");
    public static final MutableComponent TOOL_DAMAGE_PER_BLOCK = GuapiHelper.translated("cadeditor.gui.tool_damage_per_block");
    public static final MutableComponent TOOL_RULE = GuapiHelper.translated("cadeditor.gui.tool_rule");
    public static final MutableComponent TOOL_RULE_HELP = GuapiHelper.translated("cadeditor.gui.tool_rule_help");
    public static final MutableComponent MAP = GuapiHelper.translated("cadeditor.gui.map");
    public static final MutableComponent MAP_ID_TOGGLE = GuapiHelper.translated("cadeditor.gui.map_id_toggle");
    public static final MutableComponent MAP_ID_VALUE = GuapiHelper.translated("cadeditor.gui.map_id_value");
    public static final MutableComponent MAP_COLOR = GuapiHelper.translated("cadeditor.gui.map_color");
    public static final MutableComponent MAP_LOCK = GuapiHelper.translated("cadeditor.gui.map_lock");
    public static final MutableComponent MAP_DECORATION = GuapiHelper.translated("cadeditor.gui.map_decoration");
    public static final MutableComponent MAP_DECORATION_NAME = GuapiHelper.translated("cadeditor.gui.map_decoration_name");
    public static final MutableComponent MAP_DECORATION_TYPE = GuapiHelper.translated("cadeditor.gui.map_decoration_type");
    public static final MutableComponent MAP_DECORATION_X = GuapiHelper.translated("cadeditor.gui.map_decoration_x");
    public static final MutableComponent MAP_DECORATION_Z = GuapiHelper.translated("cadeditor.gui.map_decoration_z");
    public static final MutableComponent MAP_DECORATION_ROTATION = GuapiHelper.translated("cadeditor.gui.map_decoration_rotation");
    public static final MutableComponent MAP_DECORATION_PRESET = GuapiHelper.translated("cadeditor.gui.map_decoration_preset");
    public static final MutableComponent CROSSBOW = GuapiHelper.translated("cadeditor.gui.crossbow");
    public static final MutableComponent CROSSBOW_PROJECTILE = GuapiHelper.translated("cadeditor.gui.crossbow_projectile");
    public static final MutableComponent CROSSBOW_PROJECTILE_HELP = GuapiHelper.translated("cadeditor.gui.crossbow_projectile_help");
    public static final MutableComponent LODESTONE = GuapiHelper.translated("cadeditor.gui.lodestone");
    public static final MutableComponent LODESTONE_TARGET_TOGGLE = GuapiHelper.translated("cadeditor.gui.lodestone_target");
    public static final MutableComponent DIMENSION = GuapiHelper.translated("cadeditor.gui.dimension");
    public static final MutableComponent POSITION_X = GuapiHelper.translated("cadeditor.gui.position_x");
    public static final MutableComponent POSITION_Y = GuapiHelper.translated("cadeditor.gui.position_y");
    public static final MutableComponent POSITION_Z = GuapiHelper.translated("cadeditor.gui.position_z");
    public static final MutableComponent LODESTONE_TRACKED = GuapiHelper.translated("cadeditor.gui.lodestone_tracked");
    public static final MutableComponent CONTAINER_CONTENTS = GuapiHelper.translated("cadeditor.gui.container_contents");
    public static final MutableComponent CONTAINER_SLOT = GuapiHelper.translated("cadeditor.gui.container_slot_data");
    public static final MutableComponent BUNDLE_CONTENTS = GuapiHelper.translated("cadeditor.gui.bundle_contents");
    public static final MutableComponent POT_DECORATIONS = GuapiHelper.translated("cadeditor.gui.pot_decorations");
    public static final MutableComponent POT_BACK = GuapiHelper.translated("cadeditor.gui.pot_back");
    public static final MutableComponent POT_LEFT = GuapiHelper.translated("cadeditor.gui.pot_left");
    public static final MutableComponent POT_RIGHT = GuapiHelper.translated("cadeditor.gui.pot_right");
    public static final MutableComponent POT_FRONT = GuapiHelper.translated("cadeditor.gui.pot_front");
    public static final MutableComponent TRIM_PATTERN = GuapiHelper.translated("cadeditor.gui.trim_pattern");
    public static final MutableComponent TRIM_MATERIAL = GuapiHelper.translated("cadeditor.gui.trim_material");
    public static final MutableComponent TRIM_SHOW_TOOLTIP = GuapiHelper.translated("cadeditor.gui.trim_show_tooltip");
    public static final MutableComponent INSTRUMENT = GuapiHelper.translated("cadeditor.gui.instrument");
    public static final MutableComponent OMINOUS_BOTTLE = GuapiHelper.translated("cadeditor.gui.ominous_bottle");
    public static final MutableComponent OMINOUS_BOTTLE_AMPLIFIER = GuapiHelper.translated("cadeditor.gui.ominous_bottle_amplifier");
    public static final MutableComponent BANNER = GuapiHelper.translated("cadeditor.gui.banner");
    public static final MutableComponent BANNER_BASE_COLOR = GuapiHelper.translated("cadeditor.gui.banner_base_color");
    public static final MutableComponent BANNER_LAYER = GuapiHelper.translated("cadeditor.gui.banner_layer");
    public static final MutableComponent BUCKET_ENTITY = GuapiHelper.translated("cadeditor.gui.bucket_entity");
    public static final MutableComponent BUCKET_USE_ADVANCED = GuapiHelper.translated("cadeditor.gui.bucket_use_advanced");
    public static final MutableComponent BUCKET_AXOLOTL_VARIANT = GuapiHelper.translated("cadeditor.gui.bucket_axolotl_variant");
    public static final MutableComponent BUCKET_TROPICAL_PATTERN = GuapiHelper.translated("cadeditor.gui.bucket_tropical_pattern");
    public static final MutableComponent BUCKET_TROPICAL_BODY_COLOR = GuapiHelper.translated("cadeditor.gui.bucket_tropical_body_color");
    public static final MutableComponent BUCKET_TROPICAL_PATTERN_COLOR = GuapiHelper.translated("cadeditor.gui.bucket_tropical_pattern_color");
    public static final MutableComponent BUCKET_ENTITY_DATA = GuapiHelper.translated("cadeditor.gui.bucket_entity_data");
    public static final MutableComponent NOTE_BLOCK_SOUND_CATEGORY = GuapiHelper.translated("cadeditor.gui.note_block_sound_category");
    public static final MutableComponent NOTE_BLOCK_SOUND = GuapiHelper.translated("cadeditor.gui.note_block_sound");
    public static final MutableComponent SOUND_EVENT = GuapiHelper.translated("cadeditor.gui.sound_event");
    public static final MutableComponent SOUND_FILTER_ALL = GuapiHelper.translated("cadeditor.gui.sound_filter_all");
    public static final MutableComponent FIREWORK_STAR = GuapiHelper.translated("cadeditor.gui.firework_star");
    public static final MutableComponent FIREWORK_ROCKET = GuapiHelper.translated("cadeditor.gui.firework_rocket");
    public static final MutableComponent FIREWORK_SHAPE = GuapiHelper.translated("cadeditor.gui.firework_shape");
    public static final MutableComponent FIREWORK_TRAIL = GuapiHelper.translated("cadeditor.gui.firework_trail");
    public static final MutableComponent FIREWORK_TWINKLE = GuapiHelper.translated("cadeditor.gui.firework_twinkle");
    public static final MutableComponent FIREWORK_ADD_PRIMARY_COLOR = GuapiHelper.translated("cadeditor.gui.firework_add_primary_color");
    public static final MutableComponent FIREWORK_ADD_FADE_COLOR = GuapiHelper.translated("cadeditor.gui.firework_add_fade_color");
    public static final MutableComponent FIREWORK_ADD_EXPLOSION = GuapiHelper.translated("cadeditor.gui.firework_add_explosion");
    public static final MutableComponent FIREWORK_FLIGHT_DURATION = GuapiHelper.translated("cadeditor.gui.firework_flight_duration");

    
    public static class Literal {
        public static final MutableComponent BYTE = GuapiHelper.text("Byte").withStyle(ChatFormatting.BLUE);
        public static final MutableComponent BYTE_ARRAY = GuapiHelper.text("Byte Array").withStyle(ChatFormatting.BLUE);
        public static final MutableComponent COMPOUND = GuapiHelper.text("Compound").withStyle(ChatFormatting.LIGHT_PURPLE);
        public static final MutableComponent DOUBLE = GuapiHelper.text("Double").withStyle(ChatFormatting.YELLOW);
        public static final MutableComponent FLOAT = GuapiHelper.text("Float").withStyle(ChatFormatting.LIGHT_PURPLE);
        public static final MutableComponent LIST = GuapiHelper.text("List").withStyle(ChatFormatting.GREEN);
        public static final MutableComponent HEX = GuapiHelper.text("Hex");
        public static final MutableComponent INT = GuapiHelper.text("Int").withStyle(ChatFormatting.AQUA);
        public static final MutableComponent INT_ARRAY = GuapiHelper.text("Int Array").withStyle(ChatFormatting.AQUA);
        public static final MutableComponent LONG = GuapiHelper.text("Long").withStyle(ChatFormatting.RED);
        public static final MutableComponent LONG_ARRAY = GuapiHelper.text("Long Array").withStyle(ChatFormatting.RED);
        public static final MutableComponent SHORT = GuapiHelper.text("Short").withStyle(ChatFormatting.GREEN);
        public static final MutableComponent STRING = GuapiHelper.text("String").withStyle(ChatFormatting.GRAY);
    }

    public static MutableComponent errorPermissionDenied(MutableComponent with) {
        return GuapiHelper.translated("cadeditor.message.error_permission_denied", with).withStyle(ChatFormatting.RED);
    }

    public static MutableComponent errorServerModRequiredEntity() {
        return GuapiHelper.translated("cadeditor.message.error_server_mod_entity").withStyle(ChatFormatting.RED);
    }

    public static MutableComponent errorServerModRequiredBlock() {
        return GuapiHelper.translated("cadeditor.message.error_server_mod_block").withStyle(ChatFormatting.RED);
    }

    public static MutableComponent errorServerModCreativeHint(MutableComponent with) {
        return GuapiHelper.translated("cadeditor.message.error_server_mod_creative_hint", with).withStyle(ChatFormatting.RED);
    }

    public static MutableComponent commandMustInstall() {
        return GuapiHelper.translated("cadeditor.message.command_must_install").withStyle(ChatFormatting.RED);
    }

    public static MutableComponent commandDownload() {
        return GuapiHelper.translated("cadeditor.message.command_download").withStyle(style -> style
                .withClickEvent(new net.minecraft.network.chat.ClickEvent.OpenUrl(java.net.URI.create("https://www.curseforge.com/minecraft/mc-mods/cad-editor")))
                .withColor(ChatFormatting.AQUA)
                .withUnderlined(true));
    }

    public static MutableComponent commandCreativeOnly() {
        return GuapiHelper.translated("cadeditor.message.command_creative_only").withStyle(ChatFormatting.RED);
    }

    public static MutableComponent choose(MutableComponent with) {
        return GuapiHelper.translated("cadeditor.gui.choose", with);
    }

    public static MutableComponent addTag(String color, String with) {
        MutableComponent text = GuapiHelper.translated("cadeditor.gui.add_tag", GuapiHelper.text(with));
        TextColor.parseColor(color).result().ifPresent(c -> {
            text.withStyle(style -> style.withColor(c));
        });
        return text;
    }

    public static MutableComponent editorTitle(MutableComponent type) {
        return title(GuapiHelper.translated("cadeditor.gui.editor_title", type));
    }

    public static MutableComponent editorTitle(String type) {
        return editorTitle(GuapiHelper.text(type));
    }

    public static MutableComponent title(MutableComponent text) {
        return text.withStyle(new ChatFormatting[]{ChatFormatting.AQUA, ChatFormatting.BOLD});
    }

    public static MutableComponent addListEntry(MutableComponent with) {
        return GuapiHelper.translated("cadeditor.gui.add", with);
    }

    public static MutableComponent lore(int i) {
        return GuapiHelper.translated("cadeditor.gui.lore", GuapiHelper.text(Integer.toString(i)));
    }

    public static MutableComponent trade(int i) {
        return GuapiHelper.translated("cadeditor.gui.trade", GuapiHelper.text(Integer.toString(i)));
    }

    public static MutableComponent useAnimationOption(ItemUseAnimation animation) {
        return GuapiHelper.translated("cadeditor.gui.use_animation.option." + animation.getSerializedName());
    }

    public static MutableComponent equipmentSlot(EquipmentSlot slot) {
        return GuapiHelper.translated("cadeditor.gui.equipment_slot." + slot.getName());
    }

    public static MutableComponent direction(Direction direction) {
        return GuapiHelper.translated("cadeditor.gui.direction." + direction.getSerializedName());
    }

    public static MutableComponent gui(String s) {
        return GuapiHelper.translated("cadeditor.gui." + s);
    }

    public static MutableComponent attributeModifierOperationText(int value) {
        return GuapiHelper.text("OP: " + value);
    }

    public static MutableComponent attributeModifierOperationTooltip(int value) {
        return GuapiHelper.translated("cadeditor.gui.operation", GuapiHelper.translated("cadeditor.gui.operation." + value));
    }

    public static MutableComponent hide(MutableComponent with) {
        return GuapiHelper.translated("cadeditor.gui.hide", with);
    }

    public static MutableComponent[] savedVault(MutableComponent arg) {
        return arrayTextWithArg("cadeditor.gui.saved_vault", 4, arg);
    }

    public static MutableComponent copyCommand(String command) {
        return GuapiHelper.translated("cadeditor.gui.copy_command", command);
    }

    public static MutableComponent todoPlaceholder(Component feature) {
        return GuapiHelper.translated("cadeditor.gui.todo_placeholder", feature);
    }

    public static MutableComponent[] commandCopied(String arg) {
        return arrayTextWithArg("cadeditor.gui.command_copied", 4, arg);
    }

    public static MutableComponent[] wikiTooltip(String key, int lines) {
        return arrayText("cadeditor.gui.wiki." + key, lines);
    }

    public static MutableComponent give(MutableComponent with) {
        return GuapiHelper.translated("cadeditor.gui.give", with);
    }

    public static MutableComponent soundFilterNamespace(String namespace) {
        return GuapiHelper.translated("cadeditor.gui.sound_filter_namespace", namespace);
    }

    public static MutableComponent soundFilterCategory(MutableComponent category) {
        return GuapiHelper.translated("cadeditor.gui.sound_filter_category", category);
    }

    public static MutableComponent soundFilterNamespaceAll() {
        return GuapiHelper.translated("cadeditor.gui.sound_filter_namespace_all");
    }

    public static MutableComponent soundFilterCategoryAll() {
        return GuapiHelper.translated("cadeditor.gui.sound_filter_category_all");
    }

    public static MutableComponent fireworkPrimaryColor(int index) {
        return GuapiHelper.translated("cadeditor.gui.firework_color_primary", index);
    }

    public static MutableComponent fireworkFadeColor(int index) {
        return GuapiHelper.translated("cadeditor.gui.firework_color_fade", index);
    }

    public static MutableComponent fireworkExplosion(int index) {
        return GuapiHelper.translated("cadeditor.gui.firework_explosion", index);
    }

    public static MutableComponent fireworkRemoveExplosion(int index) {
        return GuapiHelper.translated("cadeditor.gui.firework_remove_explosion", index);
    }

    private static MutableComponent[] arrayText(String key, int size) {
        MutableComponent[] array = new MutableComponent[size];
        for (int i = 0; i < size; i++) {
            array[i] = GuapiHelper.translated(key + "." + i);
        }
        return array;
    }

    public static MutableComponent[] arrayTextWithArg(String key, int size, Object arg) {
        MutableComponent[] array = new MutableComponent[size];
        int i = 0;
        while (i < size) {
            array[i] = i == 0 ? GuapiHelper.translated(key + "." + i, arg) : GuapiHelper.translated(key + "." + i);
            i++;
        }
        return array;
    }

    
    public static class Messages {
        public static final MutableComponent ERROR_GENERIC = prefixed(GuapiHelper.translated("cadeditor.message.error_generic")).withStyle(ChatFormatting.RED);
        public static final MutableComponent ITEM_PARSE_FAILED = prefixed(GuapiHelper.translated("cadeditor.message.item_parse_failed")).withStyle(ChatFormatting.RED);
        public static final MutableComponent NO_DATA = prefixed(GuapiHelper.translated("cadeditor.message.no_data")).withStyle(ChatFormatting.RED);
        public static final MutableComponent VAULT_ITEM_GIVE_SUCCESS = prefixed(GuapiHelper.translated("cadeditor.message.vault_item_give_success")).withStyle(ChatFormatting.GREEN);
        public static final MutableComponent VAULT_ITEM_GIVE_FULL = prefixed(GuapiHelper.translated("cadeditor.message.vault_item_give_full")).withStyle(ChatFormatting.RED);

        public static MutableComponent vaultItemGivePartial(int count) {
            return prefixed(GuapiHelper.translated("cadeditor.message.vault_item_give_partial", count)).withStyle(ChatFormatting.YELLOW);
        }

        public static MutableComponent successUpdate(MutableComponent arg) {
            return prefixed(GuapiHelper.translated("cadeditor.message.success_update", arg)).withStyle(ChatFormatting.GREEN);
        }

        public static MutableComponent errorPermissionDenied(MutableComponent arg) {
            return prefixed(GuapiHelper.translated("cadeditor.message.error_permission_denied_chat", arg)).withStyle(ChatFormatting.RED);
        }

        public static MutableComponent errorServerModRequiredEntity() {
            return prefixed(GuapiHelper.translated("cadeditor.message.error_server_mod_entity")).withStyle(ChatFormatting.RED);
        }

        public static MutableComponent errorServerModRequiredBlock() {
            return prefixed(GuapiHelper.translated("cadeditor.message.error_server_mod_block")).withStyle(ChatFormatting.RED);
        }

        public static MutableComponent errorServerModRequiredVault() {
            return prefixed(GuapiHelper.translated("cadeditor.message.error_server_mod_vault")).withStyle(ChatFormatting.RED);
        }

        public static MutableComponent errorServerModCreativeHint(MutableComponent arg) {
            return prefixed(GuapiHelper.translated("cadeditor.message.error_server_mod_creative_hint", arg)).withStyle(ChatFormatting.RED);
        }

        public static MutableComponent errorNoTargetFound(MutableComponent arg) {
            return prefixed(GuapiHelper.translated("cadeditor.message.no_target_found", arg)).withStyle(ChatFormatting.RED);
        }

        public static MutableComponent warnNotImplemented(MutableComponent arg) {
            return prefixed(GuapiHelper.translated("cadeditor.message.not_implemented", arg)).withStyle(ChatFormatting.YELLOW);
        }

        public static MutableComponent successSavedVault(MutableComponent arg) {
            return prefixed(GuapiHelper.translated("cadeditor.message.saved_vault", arg)).withStyle(ChatFormatting.GREEN);
        }

        public static MutableComponent warnNotSavedVault(MutableComponent arg) {
            return prefixed(GuapiHelper.translated("cadeditor.message.not_saved_vault", arg)).withStyle(ChatFormatting.YELLOW);
        }

        public static MutableComponent successCopyClipboard(String arg) {
            return prefixed(GuapiHelper.translated("cadeditor.message.copied_clipboard", arg)).withStyle(ChatFormatting.GREEN);
        }

        public static MutableComponent successCopyGiveCommand() {
            return prefixed(GuapiHelper.translated("cadeditor.message.copied_give_clipboard")).withStyle(ChatFormatting.GREEN);
        }

        public static MutableComponent successCopyGiveCommandSanitized(int replacedCount, String replacementValue) {
            return prefixed(GuapiHelper.translated("cadeditor.message.copied_give_clipboard_sanitized", replacedCount, replacementValue)).withStyle(ChatFormatting.YELLOW);
        }

        public static MutableComponent toolRuleHelp() {
            return prefixed(GuapiHelper.translated("cadeditor.message.tool_rule_help")).withStyle(ChatFormatting.YELLOW);
        }

        public static MutableComponent crossbowProjectileHelp() {
            return prefixed(GuapiHelper.translated("cadeditor.message.crossbow_projectile_help")).withStyle(ChatFormatting.YELLOW);
        }

        public static MutableComponent containerLootHint() {
            return prefixed(GuapiHelper.translated("cadeditor.message.container_loot_hint")).withStyle(ChatFormatting.YELLOW);
        }

        public static MutableComponent containerLootExample() {
            return prefixed(GuapiHelper.translated("cadeditor.message.container_loot_example")).withStyle(ChatFormatting.YELLOW);
        }

        public static MutableComponent snbtInvalidCannotApply() {
            return prefixed(GuapiHelper.translated("cadeditor.message.snbt_invalid_cannot_apply")).withStyle(ChatFormatting.RED);
        }

        public static MutableComponent potDecorationInvalid() {
            return prefixed(GuapiHelper.translated("cadeditor.message.pot_decoration_invalid")).withStyle(ChatFormatting.RED);
        }

        private static MutableComponent prefixed(MutableComponent arg) {
            return GuapiHelper.translated("chat.type.announcement", GuapiHelper.translated("cadeditor"), arg);
        }
    }
}
