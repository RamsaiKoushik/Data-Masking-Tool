import axios from "axios";

export const generateDump = async (xmlConfig) => {
    //ProcessDataDump
    const response = await axios.post(import.meta.env.VITE_BACKEND_URL + "/ProcessDataDump", xmlConfig, {
        headers: {
            'Content-Type': 'application/xml',
          }
    });
    return response.data.filename;
}

