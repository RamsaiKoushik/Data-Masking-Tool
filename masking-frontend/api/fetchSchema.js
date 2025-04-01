import axios from "axios";

export const fetchSchema = async ({dbUrl,username,password}, setError, setSchema) => {
    try {
        const response = await axios.get(import.meta.env.VITE_BACKEND_URL + "/schema", {
            params: { dbUrl, username, password },
        });
        setSchema(response.data);
    } catch (err) {
        console.log(err)
        setError("Failed to fetch schema. Check credentials or server.");
    }
}