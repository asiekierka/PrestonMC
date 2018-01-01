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
