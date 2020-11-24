package org.girlscouts.common.osgi.component;

public interface CouncilCodeToPathMapper {

	public String getCouncilCode(String path);
	public String getCouncilPath(String code);

}
