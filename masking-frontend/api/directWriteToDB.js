import axios from "axios";

export const directWriteToDB = async (newDbUrl, dbName, username, password, xmlContent) => {

    if (!newDbUrl.endsWith("/")) {
        newDbUrl += "/";
    }
    
    try{
        const response = await axios.post(import.meta.env.VITE_BACKEND_URL + "/ProcessData", xmlContent, {
            headers: {
                'Content-Type': 'application/xml',
            },
            params: {
                db_url: newDbUrl,
                db_name_new: dbName,
                username: username,
                password: password
            }
        });
        return true;
    }
    catch {
        console.log("HI")
        return false;
    }
    
}

