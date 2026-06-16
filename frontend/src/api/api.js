import axios from "axios";

const apiClient = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/api`,
});

// AUTO INJECT ROLE HEADER
apiClient.interceptors.request.use((config) => {

  const user = localStorage.getItem("user");

  if (user) {
    try {
      const parsedUser = JSON.parse(user);

      config.headers.Role = parsedUser.role;
      config.headers["User-Id"] = parsedUser.id;
      
    } catch (e) {
      console.error("Failed parsing user");
    }
  }

  return config;
});

// UPDATE PROFILE
export const updateProfile = async (id, data) => {
  const response = await apiClient.put(`/users/profile/${id}`, data);
  return response.data;
};

// GET ALL USERS (Admin)
export const getAllUsers = async () => {
  const res = await apiClient.get("/users");
  return res.data;
};

// DELETE USER
export const deleteUser = async (id) => {
  const res = await apiClient.delete(`/users/${id}`);
  return res.data;
};

export const updateUserRole = async (
  id,
 roleId
) => {

  const response =
    await apiClient.put(
      `/users/${id}/role`,
      {
        roleId
      }
    );

  return response.data;
};

export const getNotifSettings = async (userId) => {
  const res = await apiClient.get(
    `/notif-settings/${userId}`
  );

  return res.data;
};

export const saveNotifSettings = async (
  userId,
  data
) => {

  const res = await apiClient.put(
    `/notif-settings/${userId}`,
    data
  );

  return res.data;
};

export const getNotifications = async (userId) => {
  const res = await apiClient.get(
    `/notifications/${userId}`
  );

  return res.data;
};

export const readNotification = async (id) => {
  await apiClient.put(
    `/notifications/${id}/read`
  );
};

export default apiClient;