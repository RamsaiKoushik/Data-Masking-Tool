// f0278b31-ea60-48f5-8914-36d5c8b1c5b6.sql
import axios from "axios";

export const generateDumpFromKey = async (dumpKey) => {
    const response = await axios.get(import.meta.env.VITE_BACKEND_URL + "/download", {
        params:{
            'filename': dumpKey
        }
    });
    return response.data
}
