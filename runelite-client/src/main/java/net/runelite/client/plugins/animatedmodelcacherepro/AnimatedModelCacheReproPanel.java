package net.runelite.client.plugins.animatedmodelcacherepro;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.*;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

class AnimatedModelCacheReproPanel extends PluginPanel
{
	AnimatedModelCacheReproPanel(AnimatedModelCacheReproPlugin plugin)
	{
		setLayout(new BorderLayout());

		JPanel container = new JPanel();
		container.setBackground(ColorScheme.DARK_GRAY_COLOR);
		container.setLayout(new GridLayout(0, 1, 3, 3));

		container.add(new JLabel("Step 1"));
		JButton animBtn = new JButton("Play Animation");
		animBtn.addActionListener(e -> plugin.playMiningAnimation());
		container.add(animBtn);
		container.add(new JLabel("<html>This loads the current equipment colors into animated model cache.</html>"));

		container.add(new JLabel("Step 2"));
		JButton cancelAnimBtn = new JButton("Cancel Animation");
		cancelAnimBtn.addActionListener(e -> plugin.cancelAnimation());
		container.add(cancelAnimBtn);

		container.add(new JLabel("Step 3"));
		JButton recolorBtn = new JButton("Recolor Gear to black");
		recolorBtn.addActionListener(e -> plugin.recolorGear((short) 0));
		container.add(recolorBtn);
		container.add(new JLabel("<html>If you don't cancel the animation this won't be visible until you cancel it (due to the cache bug).</html>"));

		container.add(new JLabel("Step 4"));
		JButton animBtn2 = new JButton("Play Animation again");
		animBtn2.addActionListener(e -> plugin.playMiningAnimation());
		container.add(animBtn2);
		container.add(new JLabel("<html>All gear will revert to original color during animation (due to the cache bug).</html>"));

		add(container, BorderLayout.NORTH);
	}
}
