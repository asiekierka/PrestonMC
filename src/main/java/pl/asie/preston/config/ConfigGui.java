/*
 * Copyright (c) 2015, 2016, 2017 Adrian Siekierka
 *
 * This file is part of Charset.
 *
 * Charset is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Charset is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Charset.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.asie.preston.config;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import pl.asie.preston.PrestonMod;

import java.util.ArrayList;
import java.util.List;

public class ConfigGui extends GuiConfig {
	public ConfigGui(GuiScreen parentScreen) {
		super(parentScreen, getConfigElements(), PrestonMod.MODID, "Preston", false, true, I18n.format("config.preston.title"));
	}

	public static List<IConfigElement> generateList(ConfigCategory category) {
		List<IConfigElement> list = new ArrayList<>();
		for (Property prop : category.values()) {
			list.add(new ConfigElement(prop));
		}
		return list;
	}

	public static List<IConfigElement> generateList(Configuration config) {
		List<IConfigElement> list = new ArrayList<>();
		for (String name : config.getCategoryNames()) {
			ConfigCategory category = config.getCategory(name);
			list.add(new DummyConfigElement.DummyCategoryElement(category.getName(), category.getLanguagekey(), generateList(category)));
		}
		return list;
	}

	private static List<IConfigElement> getConfigElements() {
		Configuration config = PrestonMod.config;
		return generateList(config);
	}
}
