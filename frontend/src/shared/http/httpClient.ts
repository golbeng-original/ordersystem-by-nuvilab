import axios from 'axios';

class HttpClient {

    private _hostUrl: string;
    private _authorizationToken: string | null = null;

    constructor(hostUrl: string) {
        this._hostUrl = hostUrl;
    }

    public updateAuthorization(token: string)  {
        this._authorizationToken = token;
    }

    public clearAuthorization() {
        this._authorizationToken = null;
    }

    public async post(params:{
        path:string,
        body: { [key:string] : any },
    }) {

        const { path, body } = params;

        const headers = this._generateHeaders();
    
        const response = await axios.post(
            `${this._hostUrl}${path}`, 
            body,
            {
                headers: headers
            }
        );
    
        return response;
    }

    public async get(params:{
        path:string
    }) {
        const { path } = params;
        const headers = this._generateHeaders();

        const response = await axios.get(
            `${this._hostUrl}${path}`,
            {
                headers: headers
            }
        );
    
        return response;
    }

    public async redirectOtherHost(params:{
        url: string,
        urlSearchParams?: URLSearchParams
    }) {
        const { url, urlSearchParams } = params;

        let href = url
        if( urlSearchParams ) {
            href += `?${urlSearchParams.toString()}`; 
        }

        window.location.href = href;
    }

    private _generateHeaders() {

        const basisHeaders = {
            'Content-Type': 'application/json'
        }

        if( !this._authorizationToken ) {
            return basisHeaders
        }

        return {
            ...basisHeaders,
            'Authorization': `Bearer ${this._authorizationToken}`,
        }
    }
}

export default HttpClient;