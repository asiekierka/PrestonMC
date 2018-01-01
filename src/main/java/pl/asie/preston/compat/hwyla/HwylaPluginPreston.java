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

package pl.asie.preston.compat.hwyla;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import pl.asie.preston.machine.BlockCompressor;
import pl.asie.preston.machine.TileCompressor;

@WailaPlugin
public class HwylaPluginPreston implements IWailaPlugin {
	@Override
	public void register(IWailaRegistrar registrar) {
		HUDHandlerCompressor handlerCompressor = new HUDHandlerCompressor();
		registrar.registerBodyProvider(handlerCompressor, TileCompressor.class);
		registrar.registerNBTProvider(handlerCompressor, TileCompressor.class);
		registrar.addConfig("Preston", "preston.compressor", false);
	}
}
