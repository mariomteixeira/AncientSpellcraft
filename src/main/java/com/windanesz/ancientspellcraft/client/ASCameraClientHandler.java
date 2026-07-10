package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.entity.CameraDummyEntity;
import com.windanesz.ancientspellcraft.misc.ASCameraState;
import com.windanesz.ancientspellcraft.registry.ASEffects;
import com.windanesz.ancientspellcraft.registry.ASEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;

/**
 * Sistema de câmera client (1.12.2 ClientEventHandler + ControlHandler + ASFakePlayer):
 * - eagle_eye: câmera fixa 50 blocos acima do ponto de cast enquanto o efeito durar.
 * - astral_projection: câmera livre movida com frente/pulo/agachar (1 bloco a cada 2 ticks).
 * - scrying_orb: a câmera vai para a posição gravada em vez da posição do caster.
 * - farsight: zoom de FOV enquanto o cast contínuo estiver ativo.
 */
public final class ASCameraClientHandler {

    private static int cameraX, cameraY, cameraZ;
    private static double previousX, previousY, previousZ;
    private static float previousYaw, previousPitch;
    private static int inputTimeout;
    private static boolean eagleEyeEnabled;
    private static boolean astralTravelEnabled;
    private static CameraDummyEntity camera;

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) {
            eagleEyeEnabled = false;
            astralTravelEnabled = false;
            ASCameraState.scryingActive = false;
            camera = null;
            return;
        }
        if (inputTimeout > 0) inputTimeout--;

        boolean eagleEffect = player.hasEffect(ASEffects.EAGLE_EYE);
        boolean astralEffect = player.hasEffect(ASEffects.ASTRAL_PROJECTION);

        // detecção de borda = onPotionAddedEvent do 1.12.2
        if (eagleEffect && !eagleEyeEnabled) {
            eagleEyeEnabled = true;
            inputTimeout = 10;
            cameraX = (int) player.getX();
            cameraY = (int) player.getY() + 50;
            cameraZ = (int) player.getZ();
            snapPrevious();
        }
        if (!eagleEffect) eagleEyeEnabled = false;

        if (astralEffect && !astralTravelEnabled) {
            astralTravelEnabled = true;
            inputTimeout = 10;
            cameraX = (int) player.getX();
            cameraY = (int) player.getY();
            cameraZ = (int) player.getZ();
            snapPrevious();
        }
        if (!astralEffect) {
            astralTravelEnabled = false;
            ASCameraState.scryingActive = false;
        }

        if (eagleEyeEnabled || astralTravelEnabled) {
            handleAstralInput(mc, player);

            double x = cameraX, y = cameraY, z = cameraZ;
            if (ASCameraState.scryingActive && ASCameraState.scryingStoredPos != null) {
                BlockPos pos = ASCameraState.scryingStoredPos;
                x = pos.getX() + 0.5;
                y = pos.getY() + 0.5;
                z = pos.getZ() + 0.5;
            }
            if (camera == null || camera.level() != mc.level || camera.isRemoved()) {
                camera = new CameraDummyEntity(ASEntities.CAMERA_DUMMY.get(), mc.level);
                camera.setPos(x, y, z);
                snapPrevious();
                mc.level.addEntity(camera);
            }
            camera.xo = previousX;
            camera.yo = previousY;
            camera.zo = previousZ;
            camera.yRotO = previousYaw;
            camera.xRotO = previousPitch;
            camera.setPos(x, y, z);
            camera.setYRot(player.getYRot());
            camera.setXRot(player.getXRot());
            if (mc.getCameraEntity() != camera) mc.setCameraEntity(camera);

            previousX = x;
            previousY = y;
            previousZ = z;
            previousYaw = player.getYRot();
            previousPitch = player.getXRot();
        } else if (mc.getCameraEntity() instanceof CameraDummyEntity) {
            mc.setCameraEntity(player);
            if (camera != null) {
                camera.discard();
                camera = null;
            }
        }
    }

    /** Movimento do astral travel (1.12.2 ControlHandler): frente move na direção olhada, pulo sobe, agachar desce. */
    private static void handleAstralInput(Minecraft mc, LocalPlayer player) {
        if (!astralTravelEnabled || inputTimeout != 0) return;
        boolean moved = false;
        if (mc.options.keyUp.isDown()) {
            moved = true;
            switch (player.getDirection()) {
                case SOUTH -> cameraZ++;
                case NORTH -> cameraZ--;
                case EAST -> cameraX++;
                case WEST -> cameraX--;
                default -> {}
            }
        }
        if (mc.options.keyJump.isDown()) {
            moved = true;
            cameraY++;
        } else if (mc.options.keyShift.isDown()) {
            moved = true;
            cameraY--;
        }
        if (moved) inputTimeout = 1;
    }

    private static void snapPrevious() {
        Minecraft mc = Minecraft.getInstance();
        previousX = cameraX;
        previousY = cameraY;
        previousZ = cameraZ;
        if (mc.player != null) {
            previousYaw = mc.player.getYRot();
            previousPitch = mc.player.getXRot();
        }
    }

    /** Zoom do farsight (1.12.2 FOVUpdateEvent): modifier 0.1 enquanto canaliza; desliga sozinho. */
    public static void onComputeFovModifier(ComputeFovModifierEvent event) {
        if (ASCameraState.farsightActive && event.getPlayer().isUsingItem()) {
            event.setNewFovModifier(0.1F);
        } else {
            ASCameraState.farsightActive = false;
        }
    }

    private ASCameraClientHandler() {}
}
