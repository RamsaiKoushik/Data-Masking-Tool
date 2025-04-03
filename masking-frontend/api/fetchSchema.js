import axios from "axios";

export const fetchSchema = async ( dbUrl, username, password ) => {

    const response = await axios.get(import.meta.env.VITE_BACKEND_URL + "/schema", {
        params: { dbUrl, username, password },
    });
    return response.data
    
}