import React, { useContext } from 'react';
import { Routes, Route } from 'react-router-dom';
import InitialComponent from '../InitialComponent/InitialComponent';
import NotFound from '../NotFound/NotFound';
import SignUp from '../Users/SignUp/SignUp';
import SignIn from '../Users/SignIn/SignIn';
import ProjectList from '../Projects/ProjectList';
import UnitTest from '../Tests/UnitTest/UnitTest';
import JMeterTest from '../Tests/JMeterTest/JMeterTest';
import Upload from '../Projects/Upload';
import AdminSignIn from '../Admin/AdminSignIn/AdminSignIn';
import SecurityBreach from '../Admin/SecurityBreach/SecurityBreach';
import MyData from '../Users/MyData/MyData';
import UserContext from '../../Contexts/UserContext';
import UserStatistics from '../Admin/UserStatistics/UserStatistics';
import UserManagement from '../Admin/UserManagement/UserManagement';
import AdminManagement from '../Admin/AdminManagement/AdminManagement';
import TermsOfService from '../TermsOfUse/TermsOfUse';

function Body() {
  const { getUserInfo } = useContext(UserContext);
  const { role } = getUserInfo;

  return (
    <Routes>
      <Route path="/" element={<InitialComponent />} />
      <Route path="/terms" element={<TermsOfService />} />
      {role != null && role === 'TESTER' && <Route path="projects" element={<ProjectList />} />}
      {role != null && role === 'TESTER' && <Route path="uploadProject" element={<Upload />} />}
      {role != null && role === 'ADMIN' && (
        <Route path="admin">
          {role != null && role === 'ADMIN' && <Route path="newAdmin" element={<AdminSignIn />} />}
          {role != null && role === 'ADMIN' && <Route path="security" element={<SecurityBreach />} />}
          {role != null && role === 'ADMIN' && <Route path="statistics" element={<UserStatistics />} />}
          {role != null && role === 'ADMIN' && <Route path="userManagement" element={<UserManagement />} />}
          {role != null && role === 'ADMIN' && <Route path="adminManagement" element={<AdminManagement />} />}
        </Route>
      )}
      <Route path="users">
        {role == null && <Route path="signup" element={<SignUp />} />}
        {role == null && <Route path="Signin" element={<SignIn />} />}
        {role != null && <Route path="myProfile" element={<MyData />} />}
      </Route>
      {role != null && role === 'TESTER' && (
        <Route path="test">
          <Route path="unit">
            <Route path=":idProject" element={<UnitTest />} />
          </Route>
          <Route path="jmeter">
            <Route path=":idProject" element={<JMeterTest />} />
          </Route>
        </Route>
      )}
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}

export default Body;
