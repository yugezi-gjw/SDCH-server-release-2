package com.varian.oiscn.base.group;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GroupInfoPoolTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testPutGet() {
		String groupID = "groupId";
		String groupName = "groupName";
		GroupInfoPool.put(groupID, groupName);

		Assert.assertEquals("groupId", GroupInfoPool.get("GROUPNAME"));
		Assert.assertEquals("groupName", GroupInfoPool.getValue("groupId"));
	}
}
