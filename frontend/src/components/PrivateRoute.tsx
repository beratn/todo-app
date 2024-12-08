import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import axios from 'axios';

interface PrivateRouteProps {
  children: React.ReactNode;
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({ children }) => {
  const { token, logout } = useAuth();
  const [isValidating, setIsValidating] = useState(true);
  const [isValid, setIsValid] = useState(false);

  useEffect(() => {
    const validateToken = async () => {
      if (!token) {
        setIsValidating(false);
        return;
      }

      try {
        await axios.get('http://localhost:8080/api/auth/validate', {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        setIsValid(true);
      } catch (error) {
        console.error('Token validation failed:', error);
        logout();
      } finally {
        setIsValidating(false);
      }
    };

    validateToken();
  }, [token, logout]);

  if (isValidating) {
    return <div>Loading...</div>;
  }

  return isValid ? <>{children}</> : <Navigate to="/login" />;
};

export default PrivateRoute;
