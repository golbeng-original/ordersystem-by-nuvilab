import axios from 'axios';

type ResolveNewToken = (token: string) => void;

class HttpClient {

    private _hostUrl: string;
    private _authorizationToken: string | null = null;

    private _resolveNewToken: ResolveNewToken | null = null;

    constructor(hostUrl: string) {
        this._hostUrl = hostUrl;
    }

    public updateAuthorization(token: string)  {
        this._authorizationToken = token;
    }

    public clearAuthorization() {
        this._authorizationToken = null;
    }

    public registerNewTokenResolver(resolveNewToken: ResolveNewToken) {
        this._resolveNewToken = resolveNewToken;
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
                headers: headers,
                withCredentials: true
            }
        );

        this._watchNewToken(response);
    
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
                headers: headers,
                withCredentials: true
            }
        );

        this._watchNewToken(response);
    
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

    private _watchNewToken(response:Axios.AxiosXHR<unknown>) {
        
        if( ('x-new-token' in response.headers) === false) {
            return;
        }

        if( this._resolveNewToken === null ) {
            return;
        }
        
        const newToken = response.headers['x-new-token'];

        console.log('New token received:', newToken);

        this._resolveNewToken(newToken);
        this._resolveNewToken = null;

        this.updateAuthorization(newToken);
    }

    private _generateHeaders() {

        const basisHeaders = {
            'Content-Type': 'application/json',
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