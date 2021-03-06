package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

import java.util.List;

public class GameObjectsUpdateStruct extends AbstractStruct<OpcodeOut> {

	public List<GameObjectLoc> objects;
}
