package com.mrcrayfish.guns.object;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.client.render.HeldAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class GripType
{
    /**
     * A grip type designed for weapons that are held with only one hand, like a pistol
     */
    public static final GripType ONE_HANDED = new GripType(new ResourceLocation(Reference.MOD_ID, "one_handed"), new HeldAnimation()
    {
        @Override
        @OnlyIn(Dist.CLIENT)
        public void applyPlayerModelRotation(PlayerModel model, Hand hand, float aimProgress)
        {
            boolean right = Minecraft.getInstance().gameSettings.mainHand == HandSide.RIGHT ? hand == Hand.MAIN_HAND : hand == Hand.OFF_HAND;
            ModelRenderer arm = right ? model.bipedRightArm : model.bipedLeftArm;
            copyModelAngles(model.bipedHead, arm);
            arm.rotateAngleX += Math.toRadians(-70F);
        }
    }, true);

    /**
     * A grip type designed for weapons that are held with two hands, like an assault rifle
     */
    public static final GripType TWO_HANDED = new GripType(new ResourceLocation(Reference.MOD_ID, "two_handed"), new HeldAnimation()
    {
        @Override
        @OnlyIn(Dist.CLIENT)
        public void applyPlayerModelRotation(PlayerModel model, Hand hand, float aimProgress)
        {
            boolean right = Minecraft.getInstance().gameSettings.mainHand == HandSide.RIGHT ? hand == Hand.MAIN_HAND : hand == Hand.OFF_HAND;
            ModelRenderer mainArm = right ? model.bipedRightArm : model.bipedLeftArm;
            ModelRenderer secondaryArm = right ? model.bipedLeftArm : model.bipedRightArm;

            copyModelAngles(model.bipedHead, mainArm);
            copyModelAngles(model.bipedHead, secondaryArm);
            mainArm.rotateAngleX = (float) Math.toRadians(-55F + aimProgress * -30F);
            mainArm.rotateAngleY = (float) Math.toRadians((-45F + aimProgress * -20F) * (right ? 1F : -1F));
            secondaryArm.rotateAngleX = (float) Math.toRadians(-42F + aimProgress * -48F);
            secondaryArm.rotateAngleY = (float) Math.toRadians((-15F + aimProgress * 5F) * (right ? 1F : -1F));
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void applyPlayerPreRender(PlayerEntity player, Hand hand, float aimProgress, MatrixStack matrixStack, IRenderTypeBuffer buffer)
        {
            boolean right = Minecraft.getInstance().gameSettings.mainHand == HandSide.RIGHT ? hand == Hand.MAIN_HAND : hand == Hand.OFF_HAND;
            player.prevRenderYawOffset = player.prevRotationYaw + (right ? 25F : -25F) + aimProgress * (right ? 20F : -20F);
            player.renderYawOffset = player.rotationYaw + (right ? 25F : -25F) + aimProgress * (right ? 20F : -20F);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void applyHeldItemTransforms(Hand hand, float aimProgress, MatrixStack matrixStack, IRenderTypeBuffer buffer)
        {
            if(hand == Hand.MAIN_HAND)
            {
                boolean right = Minecraft.getInstance().gameSettings.mainHand == HandSide.RIGHT ? hand == Hand.MAIN_HAND : hand == Hand.OFF_HAND;
                matrixStack.translate(0, 0, 0.05);
                float invertRealProgress = 1.0F - aimProgress;
                matrixStack.rotate(Vector3f.ZP.rotationDegrees((25F * invertRealProgress) * (right ? 1F : -1F)));
                matrixStack.rotate(Vector3f.YP.rotationDegrees((30F * invertRealProgress + aimProgress * -20F) * (right ? 1F : -1F)));
                matrixStack.rotate(Vector3f.XP.rotationDegrees(25F * invertRealProgress + aimProgress * 5F));
            }
        }
    }, false);

    /**
     * A custom grip type designed for the mini gun simply due it's nature of being a completely
     * unique way to hold the weapon
     */
    public static final GripType MINI_GUN = new GripType(new ResourceLocation(Reference.MOD_ID, "mini_gun"), new HeldAnimation()
    {
        @Override
        @OnlyIn(Dist.CLIENT)
        public void applyPlayerModelRotation(PlayerModel model, Hand hand, float aimProgress)
        {
            boolean right = Minecraft.getInstance().gameSettings.mainHand == HandSide.RIGHT ? hand == Hand.MAIN_HAND : hand == Hand.OFF_HAND;
            ModelRenderer mainArm = right ? model.bipedRightArm : model.bipedLeftArm;
            ModelRenderer secondaryArm = right ? model.bipedLeftArm : model.bipedRightArm;

            mainArm.rotateAngleX = (float) Math.toRadians(-15F);
            mainArm.rotateAngleY = (float) Math.toRadians(-45F) * (right ? 1F : -1F);
            mainArm.rotateAngleZ = (float) Math.toRadians(0F);

            secondaryArm.rotateAngleX = (float) Math.toRadians(-45F);
            secondaryArm.rotateAngleY = (float) Math.toRadians(30F) * (right ? 1F : -1F);
            secondaryArm.rotateAngleZ = (float) Math.toRadians(0F);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void applyPlayerPreRender(PlayerEntity player, Hand hand, float aimProgress, MatrixStack matrixStack, IRenderTypeBuffer buffer)
        {
            boolean right = Minecraft.getInstance().gameSettings.mainHand == HandSide.RIGHT ? hand == Hand.MAIN_HAND : hand == Hand.OFF_HAND;
            player.prevRenderYawOffset = player.prevRotationYaw + 45F * (right ? 1F : -1F);
            player.renderYawOffset = player.rotationYaw + 45F * (right ? 1F : -1F);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void applyHeldItemTransforms(Hand hand, float aimProgress, MatrixStack matrixStack, IRenderTypeBuffer buffer)
        {
            if(hand == Hand.OFF_HAND)
            {
                matrixStack.translate(0, -10 * 0.0625F, 0);
                matrixStack.translate(0, 0, -2 * 0.0625F);
            }
        }
    }, false);

    /**
     * The grip type map.
     */
    private static Map<ResourceLocation, GripType> gripTypeMap = new HashMap<>();

    static
    {
        /* Registers the standard grip types when the class is loaded */
        registerType(ONE_HANDED);
        registerType(TWO_HANDED);
        registerType(MINI_GUN);
    }

    /**
     * Registers a new grip type. If the id already exists, the grip type will simply be ignored.
     *
     * @param type the instance of the grip type
     */
    public static void registerType(GripType type)
    {
        gripTypeMap.putIfAbsent(type.getId(), type);
    }

    /**
     * Gets the grip type associated the the id. If the grip type does not exist, it will default to
     * one handed.
     *
     * @param id the id of the grip type
     * @return returns an instance of the grip type or ONE_HANDED if it doesn't exist
     */
    public static GripType getType(ResourceLocation id)
    {
        return gripTypeMap.getOrDefault(id, ONE_HANDED);
    }

    private final ResourceLocation id;
    private final HeldAnimation heldAnimation;
    private final boolean renderOffhand;

    /**
     * Creates a new grip type.
     *
     * @param id the id of the grip type
     * @param heldAnimation the animation functions to apply to the held weapon
     * @param renderOffhand if this grip type allows the weapon to be rendered in the off hand
     */
    public GripType(ResourceLocation id, HeldAnimation heldAnimation, boolean renderOffhand)
    {
        this.id = id;
        this.heldAnimation = heldAnimation;
        this.renderOffhand = renderOffhand;
    }

    /**
     * Gets the id of the grip type
     */
    public ResourceLocation getId()
    {
        return this.id;
    }

    /**
     * Gets the held animation instance. Used for rendering
     */
    public HeldAnimation getHeldAnimation()
    {
        return this.heldAnimation;
    }

    /**
     * Determines if this grip type will allow the weapon to be rendered in the off hand
     */
    public boolean canRenderOffhand()
    {
        return this.renderOffhand;
    }

    /**
     * Copies the rotations from one {@link ModelRenderer} instance to another
     *
     * @param source the model renderer to grab the rotations from
     * @param dest   the model renderer to apply the rotations to
     */
    @OnlyIn(Dist.CLIENT)
    private static void copyModelAngles(ModelRenderer source, ModelRenderer dest)
    {
        dest.rotateAngleX = source.rotateAngleX;
        dest.rotateAngleY = source.rotateAngleY;
        dest.rotateAngleZ = source.rotateAngleZ;
    }
}
