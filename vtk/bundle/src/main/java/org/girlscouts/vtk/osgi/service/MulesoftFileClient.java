package org.girlscouts.vtk.osgi.service;

import org.girlscouts.vtk.rest.entity.mulesoft.TroopInfoResponseEntity;
import org.girlscouts.vtk.rest.entity.mulesoft.TroopLeadersResponseEntity;
import org.girlscouts.vtk.rest.entity.mulesoft.TroopMembersResponseEntity;
import org.girlscouts.vtk.rest.entity.mulesoft.UserInfoResponseEntity;

public interface MulesoftFileClient {

    UserInfoResponseEntity getUser(String gsGlobalId);

    TroopInfoResponseEntity getTroops(String gsGlobalId);

    TroopMembersResponseEntity getMembers(String sfTroopId);

    TroopLeadersResponseEntity getTroopLeaders(String sfTroopId);

    TroopInfoResponseEntity getServiceUnitManagerTroops();

    TroopMembersResponseEntity getServiceUnitManagerContacts();

    UserInfoResponseEntity getIndependentRegisteredMember();
}
