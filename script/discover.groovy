
/**
 * Created by IntelliJ IDEA.
 * User: Per
 * Date: 2011-02-19
 * Time: 14:13
 * To change this template use File | Settings | File Templates.
 */

logger.info("DISCOVER packet received");

// See if this packet requests an address that we can try to serve it
def requestedAddress = request.getOptionAsInetAddr(DHO_DHCP_REQUESTED_ADDRESS);
logger.info(requestedAddress);