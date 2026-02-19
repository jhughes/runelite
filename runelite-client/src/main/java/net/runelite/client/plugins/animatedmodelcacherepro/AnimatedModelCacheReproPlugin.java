package net.runelite.client.plugins.animatedmodelcacherepro;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.gameval.AnimationID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;

@PluginDescriptor(
		name = "AnimatedModelCacheRepro",
		description = "Simple Reproduction of https://github.com/runelite/runelite/issues/19899"
)
@Slf4j
public class AnimatedModelCacheReproPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientToolbar clientToolbar;

	private NavigationButton navButton;

	@Override
	protected void startUp()
	{
		AnimatedModelCacheReproPanel panel = new AnimatedModelCacheReproPanel(this);

		navButton = NavigationButton.builder()
				.tooltip("Animated Model Cache Repro")
				.icon(createWhiteIcon())
				.priority(10)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
	}

	private static BufferedImage createWhiteIcon()
	{
		BufferedImage icon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = icon.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 16, 16);
		g.dispose();
		return icon;
	}

	void recolorGear(short color)
	{
		clientThread.invoke(() ->
		{
			Player localPlayer = client.getLocalPlayer();
			if (localPlayer == null)
			{
				return;
			}

			PlayerComposition comp = localPlayer.getPlayerComposition();
			if (comp == null)
			{
				return;
			}

			int[] equipmentIds = comp.getEquipmentIds();
			for (int i = 0; i < equipmentIds.length; i++)
			{
				if (equipmentIds[i] < PlayerComposition.ITEM_OFFSET)
				{
					continue;
				}

				int itemId = equipmentIds[i] - PlayerComposition.ITEM_OFFSET;
				ItemComposition itemComp = client.getItemDefinition(itemId);

				var inventoryModelData = client.loadModelData(client.getItemDefinition(itemId).getInventoryModel());
				var modelColorSet = new HashSet<Short>();
				for (var modelColor : inventoryModelData.getFaceColors()) {
					modelColorSet.add(modelColor);
				}
				short[] baseColors = toShortArray(modelColorSet);
				short[] replaceColors = new short[baseColors.length];
				Arrays.fill(replaceColors, color);

				itemComp.setColorToReplace(baseColors);
				itemComp.setColorToReplaceWith(replaceColors);
			}

			// setHash does not reset animated model cache.
			comp.setHash();
		});
	}

	static <T extends Collection<Short>> short[] toShortArray(T collection)
	{
		short[] array = new short[collection.size()];
		int i = 0;
		for (short s : collection)
		{
			array[i++] = s;
		}
		return array;
	}

	void playMiningAnimation()
	{
		clientThread.invoke(() ->
		{
			Player localPlayer = client.getLocalPlayer();
			if (localPlayer == null)
			{
				return;
			}

			localPlayer.setAnimation(AnimationID.HUMAN_MINING_BRONZE_PICKAXE);
			localPlayer.setAnimationFrame(0);
		});
	}

	void cancelAnimation()
	{
		clientThread.invoke(() ->
		{
			Player localPlayer = client.getLocalPlayer();
			if (localPlayer == null)
			{
				return;
			}

			localPlayer.setAnimation(localPlayer.getIdlePoseAnimation());
			localPlayer.setAnimationFrame(0);
		});
	}
}
