import axios from 'axios';

type ResolveNewToken = (token: string) => void;
type ClearToken = () => void;

class HttpClient {

    private _apiClient:Axios.AxiosInstance; 
    private _authorizationToken: string | null = null;

    private _resolveNewToken: ResolveNewToken | null = null;
    private _clearToken: ClearToken | null = null;

    constructor(hostUrl: string) {

        this._apiClient = axios.create({
            baseURL: hostUrl,
            withCredentials: true
        });

        this._apiClient.interceptors.response.use(
            (response) => {
                /*
                console.log('enter interceptors response : ', response.status)

                if( response.status === 302 ) {
                    const location = response.headers['location'];
                    window.location.href = location;
                }
                */
            
                return response
            },
            (error) => {
                if( error.response && (error.response.status === 401 || error.response.status === 403)) {
                    console.log('Unauthorized request, redirecting to login');

                    if( this._clearToken ) {
                        this._clearToken();
                    }
                    
                    window.location.href = '/';
                }

                return Promise.reject(error)
            }
        )
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

    public registerClearTokenResolver(clearToken: ClearToken) { 
        this._clearToken = clearToken;
    }

    public async post<T>(params:{
        path:string,
        body: { [key:string] : any },
    }) {

        const { path, body } = params;

        const headers = this._generateHeaders();
    
        const response = await this._apiClient.post<T>(
            path, 
            body,
            {
                headers: headers
            }
        );

        this._watchNewToken(response.headers);
    
        return response;
    }

    public async get<T>(params:{
        path:string
    }) {
        const { path } = params;
        const headers = this._generateHeaders();

        const response = await this._apiClient.get<T>(
            path,
            {
                headers: headers,
                withCredentials: true
            }
        );

        this._watchNewToken(response.headers);
    
        return response;

    }

    public async put<T>(params:{
        path:string,
        body: { [key:string] : any },
    }) {
        const { path, body } = params;

        const headers = this._generateHeaders();
        const response = await this._apiClient.put<T>(
            path,
            body,
            {
                headers: headers
            }
        )

        this._watchNewToken(response.headers);

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

    private _watchNewToken(headers:Record<string ,string>) {
        
        if( ('x-new-token' in headers) === false) {
            return;
        }

        if( this._resolveNewToken === null ) {
            return;
        }
        
        const newToken = headers['x-new-token'];

        console.log('New token received:', newToken);

        this._resolveNewToken(newToken);

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