/**
 * This script gets called every time a request goes into the server.
 *
 * User: Per
 * Date: 2011-02-19
 * Time: 12:38
 */
logger.info("onservice.groovy")

if (request != null) {
    logger.info(request.toString());
}

return true;