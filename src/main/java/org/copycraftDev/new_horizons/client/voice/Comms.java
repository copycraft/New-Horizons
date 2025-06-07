// package org.copycraftDev.new_horizons.client.voice;
//
// import de.maxhenkel.voicechat.api.VoicechatApi;
// import de.maxhenkel.voicechat.api.VoicechatApiClient;
// import de.maxhenkel.voicechat.api.events.VoiceChatServerEvent;
// import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
// import de.maxhenkel.voicechat.api.player.VoicechatServerPlayer;
// import net.fabricmc.loader.api.FabricLoader;
// import net.minecraft.server.MinecraftServer;
// import net.minecraft.server.network.ServerPlayerEntity;
//
// import java.util.*;
//
// public class RadioCommManager {
//
//     private static final Map<UUID, String> playerChannels = new HashMap<>();
//     private static final Set<UUID> commsDisabled = new HashSet<>();
//     private static VoicechatApi voicechat;
//
//     public static void init(MinecraftServer server) {
//         if (FabricLoader.getInstance().isModLoaded("voicechat")) {
//             voicechat = VoicechatApi.api();
//             voicechat.getEventApi().registerEvent(VoiceChatServerEvent.MICROPHONE, RadioCommManager::onVoicePacket);
//             System.out.println("[RadioCommManager] Voice chat hooked successfully.");
//         } else {
//             System.out.println("[RadioCommManager] Simple Voice Chat not found.");
//         }
//     }
//
//     private static void onVoicePacket(MicrophonePacketEvent event) {
//         VoicechatServerPlayer sender = event.getSender();
//         UUID senderId = sender.getUuid();
//
//         // Comms disabled?
//         if (commsDisabled.contains(senderId)) {
//             event.cancel();
//             return;
//         }
//
//         // Sender must be in a channel
//         String senderChannel = playerChannels.get(senderId);
//         if (senderChannel == null) {
//             event.cancel();
//             return;
//         }
//
//         event.setTargets(target -> {
//             List<VoicechatServerPlayer> allowed = new ArrayList<>();
//             for (VoicechatServerPlayer targetPlayer : target.getTargets()) {
//                 UUID targetId = targetPlayer.getUuid();
//                 if (!commsDisabled.contains(targetId)) {
//                     String targetChannel = playerChannels.get(targetId);
//                     if (senderChannel.equals(targetChannel)) {
//                         allowed.add(targetPlayer);
//                     }
//                 }
//             }
//             return allowed;
//         });
//     }
//
//     // === Public API ===
//
//     public static void setChannel(ServerPlayerEntity player, String channel) {
//         playerChannels.put(player.getUuid(), channel);
//     }
//
//     public static void clearChannel(ServerPlayerEntity player) {
//         playerChannels.remove(player.getUuid());
//     }
//
//     public static String getChannel(ServerPlayerEntity player) {
//         return playerChannels.getOrDefault(player.getUuid(), null);
//     }
//
//     public static void disableComms(ServerPlayerEntity player) {
//         commsDisabled.add(player.getUuid());
//     }
//
//     public static void enableComms(ServerPlayerEntity player) {
//         commsDisabled.remove(player.getUuid());
//     }
//
//     public static boolean isCommsEnabled(ServerPlayerEntity player) {
//         return !commsDisabled.contains(player.getUuid());
//     }
//
//     public static boolean isInSameChannel(ServerPlayerEntity p1, ServerPlayerEntity p2) {
//         return Objects.equals(getChannel(p1), getChannel(p2));
//     }
//
//     public static void setPlayerToItemChannel(ServerPlayerEntity player, String channel) {
//         setChannel(player, channel);
//         enableComms(player);
//     }
//
//     public static void removePlayerComms(ServerPlayerEntity player) {
//         clearChannel(player);
//         disableComms(player);
//     }
// }
//