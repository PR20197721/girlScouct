package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.rest.entity.mulesoft.TroopInfoResponseEntity;
import org.girlscouts.vtk.rest.entity.mulesoft.TroopLeadersResponseEntity;
import org.girlscouts.vtk.rest.entity.mulesoft.TroopMembersResponseEntity;
import org.girlscouts.vtk.rest.entity.mulesoft.UserInfoResponseEntity;

public interface MulesoftFileClient {

    UserInfoResponseEntity getUser(String gsGlobalId, Boolean isDemo);

    TroopInfoResponseEntity getTroops(String gsGlobalId, Boolean isDemo);

    TroopMembersResponseEntity getMembers(String sfTroopId, Boolean isDemo);

    TroopLeadersResponseEntity getTroopLeaders(String sfTroopId, Boolean isDemo);

    TroopInfoResponseEntity getServiceUnitManagerTroops();

    TroopMembersResponseEntity getServiceUnitManagerContacts();

    UserInfoResponseEntity getIndependentRegisteredMember();
}
