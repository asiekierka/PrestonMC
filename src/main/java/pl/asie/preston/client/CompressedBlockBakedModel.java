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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import pl.asie.preston.container.BlockCompressedBlock;
import pl.asie.preston.container.ItemCompressedBlock;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.*;

public class CompressedBlockBakedModel implements IBakedModel {
	private static final EnumMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformMap = new EnumMap(ItemCameraTransforms.TransformType.class);
	private final ItemOverrideList overrideList = new ItemOverrideList(Collections.emptyList()) {
		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
			if (!stack.isEmpty()) {
				return new CompressedBlockBakedModel(stack,
						Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(ItemCompressedBlock.getContained(stack), world, entity));
			} else {
				return originalModel;
			}
		}
	};

	private ItemStack stack;
	private IBakedModel itemModel;

	static {
		flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);

		TRSRTransformation thirdperson = getTransformation(0, 2.5f, 0, 75, 45, 0, 0.375f);
		addTransformation(transformMap, ItemCameraTransforms.TransformType.GUI, getTransformation(0, 0, 0, 30, 225, 0, 0.625f));
		addTransformation(transformMap, ItemCameraTransforms.TransformType.GROUND, getTransformation(0, 3, 0, 0, 0, 0, 0.25f));
		addTransformation(transformMap, ItemCameraTransforms.TransformType.FIXED, getTransformation(0, 0, 0, 0, 0, 0, 0.5f));
		addThirdPersonTransformation(transformMap, thirdperson);
		addTransformation(transformMap, ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, getTransformation(0, 0, 0, 0, 45, 0, 0.4f));
		addTransformation(transformMap, ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, getTransformation(0, 0, 0, 0, 255, 0, 0.4f));
	}

	public CompressedBlockBakedModel() {
		this.stack = null;
	}

	public CompressedBlockBakedModel(ItemStack stack, IBakedModel itemModel) {
		this.stack = stack;
		this.itemModel = itemModel;
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
		return ImmutablePair.of(this,
				transformMap.containsKey(cameraTransformType) ? transformMap.get(cameraTransformType).getMatrix() : null);
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
		CBModelCacheKey key = null;
		IBakedModel parentModel = null;

		if (stack != null) {
			key = new CBModelCacheKey(stack);
			parentModel = itemModel;
		} else if (state instanceof IExtendedBlockState) {
			key = ((IExtendedBlockState) state).getValue(BlockCompressedBlock.MODEL_CACHE_KEY);
			if (key != null) {
				parentModel = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(key.stack, null, null);
			}
		}

		if (key != null) {
			List<BakedQuad> list = parentModel.getQuads(state, side, rand);

			IBakedModel ovModel = ResourceGenerator.getOverlayModel(key.count);
			List<BakedQuad> ovList = ovModel != null ? ovModel.getQuads(state, side, rand) : Collections.emptyList();

			if (!ovList.isEmpty() && !list.isEmpty()) {
				List<BakedQuad> newList = new ArrayList<>();
				newList.addAll(list);
				newList.addAll(ovList);
				return newList;
			} else {
				return ovList.isEmpty() ? list : ovList;
			}
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return ModelLoader.defaultTextureGetter().apply(ResourceGenerator.getLocation(1));
	}

	@Override
	public ItemOverrideList getOverrides() {
		return overrideList;
	}

	public static void addTransformation(Map<ItemCameraTransforms.TransformType, TRSRTransformation> transformMap, ItemCameraTransforms.TransformType type, TRSRTransformation transformation) {
		transformMap.put(type, TRSRTransformation.blockCornerToCenter(transformation));
	}

	public static void addThirdPersonTransformation(Map<ItemCameraTransforms.TransformType, TRSRTransformation> transformMap, TRSRTransformation transformation) {
		addTransformation(transformMap, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, transformation);
		addTransformation(transformMap, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,  toLeftHand(transformation));
	}

	public static void addFirstPersonTransformation(Map<ItemCameraTransforms.TransformType, TRSRTransformation> transformMap, TRSRTransformation transformation) {
		addTransformation(transformMap, ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, transformation);
		addTransformation(transformMap, ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,  toLeftHand(transformation));
	}

	// ForgeBlockStateV1 transforms

	private static final TRSRTransformation flipX;

	protected static TRSRTransformation toLeftHand(TRSRTransformation transform) {
		return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
	}

	protected static TRSRTransformation getTransformation(float tx, float ty, float tz, float ax, float ay, float az, float s) {
		return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
				new Vector3f(tx / 16, ty / 16, tz / 16),
				TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)),
				new Vector3f(s, s, s),
				null));
	}
}
