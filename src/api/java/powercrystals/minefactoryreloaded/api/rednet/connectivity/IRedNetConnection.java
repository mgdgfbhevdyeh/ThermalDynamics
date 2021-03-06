package powercrystals.minefactoryreloaded.api.rednet.connectivity;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Defines a Block that can connect to RedNet cables. This must be implemented on your Block class.
 */
public interface IRedNetConnection
{
	/**
	 * Returns the connection type of this Block. If this value must be changed
	 * while the block is alive, it must notify neighbors of a change.
	 * <p>
	 * For nodes that want to interact with rednet,
	 * see IRedNetInputNode, IRedNetOutputNode, and IRedNetOmniNode
	 * 
	 * @param world The world this block is in.
	 * @param x This block's X coordinate.
	 * @param y This block's Y coordinate.
	 * @param z This block's Z coordinate.
	 * @param side The side that connection information is required for.
	 * @return The connection type.
	 */
    RedNetConnectionType getConnectionType(World world, int x, int y, int z, EnumFacing side);
}
