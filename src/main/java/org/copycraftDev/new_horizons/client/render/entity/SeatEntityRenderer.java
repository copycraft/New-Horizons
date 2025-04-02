package org.copycraftDev.new_horizons.client.render.entity;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.copycraftDev.new_horizons.core.entity.SeatEntity;

public class SeatEntityRenderer extends EntityRenderer<SeatEntity> {

    public SeatEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(SeatEntity entity) {
        return Identifier.of("minecraft", "textures/misc/empty.png"); // Fully transparent texture
    }
}
