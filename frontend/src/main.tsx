//import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import AppRoutes from './Routes.tsx'

import './index.css'
import { HttpClientProvider } from './shared/http/httpClientContext.tsx'


createRoot(document.getElementById('root')!).render(
	/*
	<StrictMode></StrictMode>,
	*/
	<BrowserRouter>
		<HttpClientProvider>
			<AppRoutes />
		</HttpClientProvider>
	</BrowserRouter>
)
