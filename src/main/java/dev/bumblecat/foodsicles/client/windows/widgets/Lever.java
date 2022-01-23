package dev.bumblecat.foodsicles.client.windows.widgets;

import dev.bumblecat.bumblecore.client.conveys.MouseEventArgs;
import dev.bumblecat.bumblecore.client.windows.IClientWindow;
import dev.bumblecat.bumblecore.client.windows.widgets.CheckBox;
import dev.bumblecat.bumblecore.client.windows.widgets.IWidget;

import net.minecraft.resources.ResourceLocation;

import java.awt.*;

import org.jetbrains.annotations.Nullable;

public class Lever extends CheckBox implements IWidget {

    private final ResourceLocation texture = new ResourceLocation("foodsicles", "textures/menu/lever.png");
    private int textureY = 0;

    /**
     * @param window
     * @param position
     */
    public Lever(IClientWindow window, Rectangle position) {
        super(window, position);
    }

    /**
     * @return
     */
    @Override
    public @Nullable
    ResourceLocation getTexture() {
        return this.texture;
    }

    /**
     * @return
     */
    @Override
    public Rectangle getSpritePosition() {
        return getValue() ? new Rectangle(16, textureY, 16, 48) : new Rectangle(0, textureY, 16, 48);
    }

    /**
     * @param arguments
     *
     * @return
     */
    @Override
    public boolean onMouseMoving(MouseEventArgs arguments) {
        if (!super.onMouseMoving(arguments))
            textureY = 0;

        Rectangle hitBox = new Rectangle(((int) getLocation().getX()) + 1, ((int) getLocation().getY()) + 14, 14, 22);
        if (hitBox.contains(arguments.getMousePointOnWindow()))
            textureY = 48;

        return true;
    }
}
