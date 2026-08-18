package com.onewhohears.onewholibs.client.renderer;

import com.onewhohears.onewholibs.client.model.obj.ObjEntityModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.Team;

public class RendererObjLiving<T extends LivingEntity> extends RendererObjEntity<T> {

    public RendererObjLiving(EntityRendererProvider.Context ctx, ObjEntityModel<T> model) {
        super(ctx, model);
    }

    protected boolean shouldShowName(T livingEntity) {
        double d = this.entityRenderDispatcher.distanceToSqr(livingEntity);
        float f = livingEntity.isDiscrete() ? 32.0F : 64.0F;
        if (d >= (double)(f * f)) {
            return false;
        } else {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localPlayer = minecraft.player;
            boolean bl = !livingEntity.isInvisibleTo(localPlayer);
            if (livingEntity != localPlayer) {
                Team team = livingEntity.getTeam();
                Team team2 = localPlayer.getTeam();
                if (team != null) {
                    Team.Visibility visibility = team.getNameTagVisibility();
                    switch (visibility) {
                        case ALWAYS -> {
                            return bl;
                        }
                        case NEVER -> {
                            return false;
                        }
                        case HIDE_FOR_OTHER_TEAMS -> {
                            return team2 == null ? bl : team.isAlliedTo(team2) && (team.canSeeFriendlyInvisibles() || bl);
                        }
                        case HIDE_FOR_OWN_TEAM -> {
                            return team2 == null ? bl : !team.isAlliedTo(team2) && bl;
                        }
                        default -> {
                            return true;
                        }
                    }
                }
            }

            return Minecraft.renderNames() && livingEntity != minecraft.getCameraEntity() && bl && !livingEntity.isVehicle();
        }
    }

}
