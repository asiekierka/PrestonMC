/*
 * Copyright (c) 2015, 2016, 2017 Adrian Siekierka
 *
 * This file is part of Preston.
 *
 * Preston is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Preston is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Preston.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.asie.preston.client;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.asie.preston.PrestonMod;
import pl.asie.preston.PrestonUtils;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.function.Function;

public class ResourceGenerator {
	private static final String NAME = "preston:blocks/overlay";
	private static final String NAME_PREFIX = NAME + "_";
	private static final Gson gson = new Gson();
	private static IBakedModel[] models;
	private static SettingsFile settings;

	public static class SettingsFile {
		private int version;
		private Map<String, Settings> entries;

		public Settings get(String s) {
			return entries.get(s);
		}

		public void add(SettingsFile other) {
			if (other.version == version) {
				for (String s : other.entries.keySet()) {
					if (!entries.containsKey(s)) {
						entries.put(s, other.entries.get(s));
					} else {
						entries.get(s).add(other.entries.get(s));
					}
				}
			}
		}
	}

	public static class Settings {
		public float[] color;
		public int size;

		public void add(Settings other) {
			color = other.color != null ? other.color : color;
			size = other.size > 0 ? other.size : size;
		}

		public int getColor() {
			return (Math.round(PrestonUtils.clamp(color[0], 0, 1) * 255) << 16)
					| (Math.round(PrestonUtils.clamp(color[1], 0, 1) * 255) << 8)
					| (Math.round(PrestonUtils.clamp(color[2], 0, 1) * 255));
		}
	}

	public static class Texture extends TextureAtlasSprite {
		private final int count;

		protected Texture(int count) {
			super(NAME_PREFIX + count);
			this.count = count;
		}

		@Override
		public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
			return true;
		}

		@Override
		public boolean load(IResourceManager manager, ResourceLocation loc, Function<ResourceLocation, TextureAtlasSprite> getter) {
			Settings overlaySettings = settings.get("overlay");
			setIconWidth(overlaySettings.size);
			setIconHeight(overlaySettings.size);

			int[][] pixels = new int[Minecraft.getMinecraft().gameSettings.mipmapLevels + 1][];
			pixels[0] = new int[overlaySettings.size * overlaySettings.size];

			int color = overlaySettings.getColor();
			int center = overlaySettings.size / 2;

			for (int iy = 0; iy < overlaySettings.size; iy++) {
				int yDist = iy < center ? (center - 1 - iy) : iy - center;
				for (int ix = 0; ix < overlaySettings.size; ix++) {
					int xDist = ix < center ? (center - 1 - ix) : ix - center;
					int distance = Math.max(xDist, yDist);
					float distFloat = ((float) distance / (center - 1));
					distFloat = (float) Math.pow(distFloat, ( PrestonMod.MAX_COMPRESSION_LEVELS + 1 - count) / 5.0f);

					int alpha = Math.round(distFloat * 255);
					pixels[0][iy * overlaySettings.size + ix] = color | (alpha << 24);
				}
			}

			this.clearFramesTextureData();
			this.framesTextureData.add(pixels);

			return false;
		}
	}

	@Nullable
	public static IBakedModel getOverlayModel(int v) {
		if (v <= 0) return null;
		else if (v > PrestonMod.MAX_COMPRESSION_LEVELS) return models[PrestonMod.MAX_COMPRESSION_LEVELS - 1];
		else return models[v - 1];
	}

	public static ResourceLocation getLocation(int count) {
		return new ResourceLocation(NAME_PREFIX + count);
	}

	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre event) {
		// Load texgen data
		try {
			for (IResource resource : Minecraft.getMinecraft().getResourceManager().getAllResources(new ResourceLocation("preston", "texgen_settings.json"))) {
				SettingsFile file = gson.fromJson(new InputStreamReader(resource.getInputStream()), SettingsFile.class);
				if (file != null) {
					if (settings == null) {
						settings = file;
					} else {
						settings.add(file);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Generate textures
		for (int i = 1; i <= PrestonMod.MAX_COMPRESSION_LEVELS; i++) {
			event.getMap().setTextureEntry(new Texture(i));
		}
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		try {
			IModel model = ModelLoaderRegistry.getModel(new ResourceLocation("preston", "block/overlay"));
			models = new IBakedModel[PrestonMod.MAX_COMPRESSION_LEVELS];
			for (int i = 1; i <= PrestonMod.MAX_COMPRESSION_LEVELS; i++) {
				models[i - 1] = model.retexture(ImmutableMap.of(
						"all", NAME_PREFIX + i,
						"#all", NAME_PREFIX + i
				)).bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
